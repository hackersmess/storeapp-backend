package com.storeapp.activity.repository;

import com.storeapp.activity.entity.ActivityExpenseSplit;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository per le suddivisioni delle spese
 */
@ApplicationScoped
public class ActivityExpenseSplitRepository implements PanacheRepository<ActivityExpenseSplit> {

    /**
     * Trova tutte le split di una spesa
     */
    public List<ActivityExpenseSplit> findByExpenseId(Long expenseId) {
        return list("expense.id", expenseId);
    }

    /**
     * Trova split per un membro specifico
     */
    public List<ActivityExpenseSplit> findByExpenseIdAndGroupMemberId(Long expenseId, Long groupMemberId) {
        return list("expense.id = ?1 AND groupMember.id = ?2", expenseId, groupMemberId);
    }

    /**
     * Calcola il totale delle split per una spesa
     */
    public BigDecimal getTotalByExpenseId(Long expenseId) {
        BigDecimal total = (BigDecimal) getEntityManager()
            .createQuery(
                "SELECT COALESCE(SUM(s.amount), 0) FROM ActivityExpenseSplit s WHERE s.expense.id = :id")
            .setParameter("id", expenseId)
            .getSingleResult();
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Elimina tutte le split di una spesa
     */
    public long deleteByExpenseId(Long expenseId) {
        return delete("expense.id", expenseId);
    }

    /**
     * Conta split non pagate per un membro
     */
    public long countUnsettledByGroupMemberId(Long groupMemberId) {
        return count("groupMember.id = ?1 AND isPaid = false", groupMemberId);
    }

    /**
     * Aggrega bilanci di tutti i membri per un gruppo.
     * Per ogni membro restituisce:
     *   totalPaid = SUM(paid_amount)  → quanto ha anticipato
     *   totalOwed = SUM(amount)       → la sua quota totale
     *
     * Vengono inclusi TUTTI i membri che appaiono in almeno una split
     * (sia paganti che debitori).
     */
    @SuppressWarnings("unchecked")
    public List<MemberBalanceProjection> getBalancesByGroupId(Long groupId) {
        return (List<MemberBalanceProjection>) getEntityManager().createQuery(
            "SELECT new com.storeapp.activity.repository.MemberBalanceProjection(" +
            "  aes.groupMember.id," +
            "  aes.groupMember.user.name," +
            "  aes.groupMember.user.avatarUrl," +
            "  COALESCE(SUM(aes.paidAmount), 0)," +
            "  COALESCE(SUM(aes.amount), 0)" +
            ") " +
            "FROM ActivityExpenseSplit aes " +
            "JOIN aes.expense ae " +
            "JOIN ae.activity a " +
            "WHERE a.group.id = :groupId " +
            "GROUP BY aes.groupMember.id, aes.groupMember.user.name, aes.groupMember.user.avatarUrl"
        )
        .setParameter("groupId", groupId)
        .getResultList();
    }

    /**
     * Conta il numero totale di spese di un gruppo
     */
    public long countExpensesByGroupId(Long groupId) {
        return (long) getEntityManager().createQuery(
            "SELECT COUNT(ae) FROM ActivityExpense ae JOIN ae.activity a WHERE a.group.id = :groupId"
        )
        .setParameter("groupId", groupId)
        .getSingleResult();
    }

    /**
     * Somma totale di tutte le spese di un gruppo
     */
    public BigDecimal getTotalExpensesByGroupId(Long groupId) {
        BigDecimal result = (BigDecimal) getEntityManager().createQuery(
            "SELECT COALESCE(SUM(ae.amount), 0) FROM ActivityExpense ae " +
            "JOIN ae.activity a WHERE a.group.id = :groupId"
        )
        .setParameter("groupId", groupId)
        .getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }
}
