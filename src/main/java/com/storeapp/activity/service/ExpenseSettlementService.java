package com.storeapp.activity.service;

import com.storeapp.activity.dto.GroupExpenseSettlementDto;
import com.storeapp.activity.dto.MemberBalanceDto;
import com.storeapp.activity.dto.SettleDebtRequest;
import com.storeapp.activity.dto.SettlementTransactionDto;
import com.storeapp.activity.entity.ActivityExpense;
import com.storeapp.activity.entity.ActivityExpenseSplit;
import com.storeapp.activity.entity.Event;
import com.storeapp.activity.entity.EventCategory;
import com.storeapp.activity.repository.ActivityExpenseRepository;
import com.storeapp.activity.repository.ActivityExpenseSplitRepository;
import com.storeapp.activity.repository.ActivityRepository;
import com.storeapp.activity.repository.MemberBalanceProjection;
import com.storeapp.group.entity.GroupMember;
import com.storeapp.group.repository.GroupMemberRepository;
import com.storeapp.group.repository.GroupRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servizio per il calcolo del settlement delle spese di gruppo.
 *
 * Algoritmo: Greedy Debt Simplification
 *   1. Calcola bilancio per ogni membro: paid - owed
 *   2. Separa creditori (balance > 0) e debitori (balance < 0)
 *   3. Abbina il debitore più grande col creditore più grande
 *      → genera una transazione, riduce i saldi, ripete
 *   Risultato: numero minimo di transazioni per saldare tutto
 */
@ApplicationScoped
public class ExpenseSettlementService {

    /** Nome riservato dell'activity usata per i rimborsi di saldo */
    private static final String SETTLEMENT_ACTIVITY_NAME = " Rimborsi";

    @Inject
    ActivityExpenseSplitRepository splitRepository;

    @Inject
    GroupRepository groupRepository;

    @Inject
    GroupMemberRepository groupMemberRepository;

    @Inject
    ActivityRepository activityRepository;

    @Inject
    ActivityExpenseRepository expenseRepository;

    public GroupExpenseSettlementDto calculateSettlement(Long groupId, Long userId) {
        // Verifica che il gruppo esista e l'utente ne faccia parte
        var group = groupRepository.findByIdOptional(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        if (!group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        // 1. Bilanci aggregati per membro
        List<MemberBalanceProjection> rawBalances = splitRepository.getBalancesByGroupId(groupId);

        List<MemberBalanceDto> balanceDtos = rawBalances.stream().map(p -> {
            MemberBalanceDto dto = new MemberBalanceDto();
            dto.groupMemberId = p.groupMemberId;
            dto.memberName = p.memberName;
            dto.memberAvatarUrl = p.memberAvatarUrl;
            dto.totalPaid = p.totalPaid.setScale(2, RoundingMode.HALF_UP);
            dto.totalOwed = p.totalOwed.setScale(2, RoundingMode.HALF_UP);
            dto.balance = dto.totalPaid.subtract(dto.totalOwed);
            return dto;
        }).collect(Collectors.toList());

        // 2. Algoritmo greedy
        List<SettlementTransactionDto> transactions = greedySettle(balanceDtos);

        // 3. Totali
        BigDecimal totalExpenses = splitRepository.getTotalExpensesByGroupId(groupId);
        long expenseCount = splitRepository.countExpensesByGroupId(groupId);

        GroupExpenseSettlementDto result = new GroupExpenseSettlementDto();
        result.groupId = groupId;
        result.totalExpenses = totalExpenses.setScale(2, RoundingMode.HALF_UP);
        result.expenseCount = (int) expenseCount;
        result.balances = balanceDtos;
        result.settlements = transactions;
        result.transactionCount = transactions.size();
        return result;
    }

    /**
     * Greedy Debt Simplification.
     *
     * Lavora su copie mutabili dei bilanci per non alterare i DTO originali.
     * Threshold: ignora differenze < 0.01€ (arrotondamenti floating point).
     */
    private List<SettlementTransactionDto> greedySettle(List<MemberBalanceDto> balances) {
        final BigDecimal THRESHOLD = new BigDecimal("0.01");

        // Copie mutabili: (memberId, nome, avatar, saldo corrente)
        record Node(Long id, String name, String avatarUrl, BigDecimal[] balance) {}

        List<Node> debtors = new ArrayList<>();
        List<Node> creditors = new ArrayList<>();

				// Suddivido i membri in debitori e creditori, ignorando quelli già in pari
        for (MemberBalanceDto b : balances) {
            BigDecimal bal = b.balance;
            if (bal.compareTo(THRESHOLD.negate()) < 0) {
                // debitore: balance negativo
                debtors.add(new Node(b.groupMemberId, b.memberName, b.memberAvatarUrl,
                        new BigDecimal[]{bal.abs()}));
            } else if (bal.compareTo(THRESHOLD) > 0) {
                // creditore: balance positivo
                creditors.add(new Node(b.groupMemberId, b.memberName, b.memberAvatarUrl,
                        new BigDecimal[]{bal}));
            }
            // balance ≈ 0 → già in pari, ignorato
        }

        List<SettlementTransactionDto> transactions = new ArrayList<>();

        // Ordina decrescente per importo assoluto (ottimizza le transazioni)
        Comparator<Node> desc = Comparator.comparing(n -> n.balance()[0].negate());

        while (!debtors.isEmpty() && !creditors.isEmpty()) {
            debtors.sort(desc);
            creditors.sort(desc);

            Node debtor = debtors.get(0);
            Node creditor = creditors.get(0);

						// prendi il minimo tra il saldo del debitore e quello del creditore
            BigDecimal amount = debtor.balance()[0].min(creditor.balance()[0])
                    .setScale(2, RoundingMode.HALF_UP);

            SettlementTransactionDto tx = new SettlementTransactionDto();
            tx.fromMemberId = debtor.id();
            tx.fromMemberName = debtor.name();
            tx.fromMemberAvatarUrl = debtor.avatarUrl();
            tx.toMemberId = creditor.id();
            tx.toMemberName = creditor.name();
            tx.toMemberAvatarUrl = creditor.avatarUrl();
            tx.amount = amount;
            transactions.add(tx);

            // Aggiorna saldi
            debtor.balance()[0] = debtor.balance()[0].subtract(amount);
            creditor.balance()[0] = creditor.balance()[0].subtract(amount);

            // Rimuovi se saldato
            if (debtor.balance()[0].compareTo(THRESHOLD) < 0) debtors.remove(0);
            if (creditor.balance()[0].compareTo(THRESHOLD) < 0) creditors.remove(0);
        }

        return transactions;
    }

    /**
     * Registra un pagamento di saldo: fromMember paga toMember dell'importo indicato.
     *
     * Trova (o crea) una Event di tipo "💸 Rimborsi" nel gruppo e aggiunge
     * una spesa dove il debitore è l'unico payer e il creditore è l'unico split.
     * In questo modo il bilancio aggregato si aggiorna automaticamente.
     */
    @Transactional
    public void recordSettlement(Long groupId, SettleDebtRequest request, Long userId) {
        var group = groupRepository.findByIdOptional(groupId)
                .orElseThrow(() -> new NotFoundException("Gruppo non trovato"));
        if (!group.isMember(userId)) {
            throw new RuntimeException("Non sei membro di questo gruppo");
        }

        GroupMember fromMember = groupMemberRepository.findByIdOptional(request.fromMemberId)
                .orElseThrow(() -> new NotFoundException("Membro pagante non trovato"));
        GroupMember toMember = groupMemberRepository.findByIdOptional(request.toMemberId)
                .orElseThrow(() -> new NotFoundException("Membro ricevente non trovato"));

        if (!fromMember.group.id.equals(groupId) || !toMember.group.id.equals(groupId)) {
            throw new RuntimeException("I membri non appartengono a questo gruppo");
        }

        // Trova o crea l'activity di rimborso del gruppo
        Event settlementActivity = (Event) activityRepository
                .find("group.id = ?1 AND name = ?2", groupId, SETTLEMENT_ACTIVITY_NAME)
                .firstResultOptional()
                .orElseGet(() -> {
                    Event ev = new Event();
                    ev.group = group;
                    ev.name = SETTLEMENT_ACTIVITY_NAME;
                    ev.startDate = LocalDate.now();
                    ev.endDate = LocalDate.now();
                    LocalTime now = LocalTime.now().withSecond(0).withNano(0);
                    ev.startTime = now;
                    ev.endTime = now.plusMinutes(1);
                    ev.category = EventCategory.OTHER;
                    ev.isCompleted = false;
                    ev.displayOrder = Integer.MAX_VALUE;
                    activityRepository.persist(ev);
                    return ev;
                });

        // Crea la spesa: fromMember paga, toMember riceve (split al 100%)
        String description = request.note != null && !request.note.isBlank()
                ? request.note
                : fromMember.user.getName() + " → " + toMember.user.getName();

        ActivityExpense expense = new ActivityExpense();
        expense.activity = settlementActivity;
        expense.description = description;
        expense.amount = request.amount.setScale(2, RoundingMode.HALF_UP);
        expense.currency = request.currency != null ? request.currency : "EUR";
        expense.paidBy = fromMember;
        expenseRepository.persist(expense);

        // Split 1: fromMember paga l'intero importo (payer)
        ActivityExpenseSplit payerSplit = new ActivityExpenseSplit();
        payerSplit.expense = expense;
        payerSplit.groupMember = fromMember;
        payerSplit.amount = BigDecimal.ZERO;          // non deve nulla
        payerSplit.isPayer = true;
        payerSplit.paidAmount = expense.amount;
        splitRepository.persist(payerSplit);

        // Split 2: toMember "deve" l'importo al fromMember (viene azzerato dal rimborso)
        ActivityExpenseSplit receiverSplit = new ActivityExpenseSplit();
        receiverSplit.expense = expense;
        receiverSplit.groupMember = toMember;
        receiverSplit.amount = expense.amount;        // deve ricevere questo importo
        receiverSplit.isPayer = false;
        receiverSplit.paidAmount = BigDecimal.ZERO;
        splitRepository.persist(receiverSplit);

        // Aggiorna totalCost dell'activity di rimborsi
        BigDecimal newTotal = expenseRepository.getTotalByActivityId(settlementActivity.id);
        settlementActivity.totalCost = newTotal != null ? newTotal : BigDecimal.ZERO;
        activityRepository.persist(settlementActivity);
    }
}
