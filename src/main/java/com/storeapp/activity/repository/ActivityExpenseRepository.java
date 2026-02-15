package com.storeapp.activity.repository;

import com.storeapp.activity.entity.ActivityExpense;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository per le spese delle attività
 */
@ApplicationScoped
public class ActivityExpenseRepository implements PanacheRepository<ActivityExpense> {

    /**
     * Trova tutte le spese di un'attività
     */
    public List<ActivityExpense> findByActivityId(Long activityId) {
        return list("activity.id = ?1 ORDER BY createdAt DESC", activityId);
    }

    /**
     * Trova spese pagate da un membro specifico
     */
    public List<ActivityExpense> findByActivityIdAndPaidBy(Long activityId, Long groupMemberId) {
        return list("activity.id = ?1 AND paidBy.id = ?2 ORDER BY createdAt DESC", activityId, groupMemberId);
    }

    /**
     * Calcola il totale delle spese di un'attività
     */
    public BigDecimal getTotalByActivityId(Long activityId) {
        BigDecimal total = find("SELECT SUM(e.amount) FROM ActivityExpense e WHERE e.activity.id = ?1", activityId)
            .project(BigDecimal.class)
            .firstResult();
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Elimina tutte le spese di un'attività
     */
    public long deleteByActivityId(Long activityId) {
        return delete("activity.id", activityId);
    }
}
