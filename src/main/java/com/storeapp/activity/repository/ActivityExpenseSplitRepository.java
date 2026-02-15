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
        BigDecimal total = find("SELECT SUM(s.amount) FROM ActivityExpenseSplit s WHERE s.expense.id = ?1", expenseId)
            .project(BigDecimal.class)
            .firstResult();
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
}
