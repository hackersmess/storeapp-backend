package com.storeapp.activity.repository;

import com.storeapp.activity.entity.Activity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository per le attività
 */
@ApplicationScoped
public class ActivityRepository implements PanacheRepository<Activity> {

    /**
     * Trova tutte le attività di un gruppo ordinate per display_order
     */
    public List<Activity> findByGroupId(Long groupId) {
        return list("group.id = ?1 ORDER BY displayOrder, scheduledDate, startTime", groupId);
    }

    /**
     * Trova attività per gruppo e data
     */
    public List<Activity> findByGroupIdAndDate(Long groupId, LocalDate date) {
        return list("group.id = ?1 AND scheduledDate = ?2 ORDER BY startTime", groupId, date);
    }

    /**
     * Trova attività per gruppo e intervallo di date
     */
    public List<Activity> findByGroupIdAndDateRange(Long groupId, LocalDate startDate, LocalDate endDate) {
        return list("group.id = ?1 AND scheduledDate BETWEEN ?2 AND ?3 ORDER BY scheduledDate, startTime",
            groupId, startDate, endDate);
    }

    /**
     * Conta attività di un gruppo
     */
    public long countByGroupId(Long groupId) {
        return count("group.id", groupId);
    }

    /**
     * Conta attività completate di un gruppo
     */
    public long countCompletedByGroupId(Long groupId) {
        return count("group.id = ?1 AND isCompleted = true", groupId);
    }

    /**
     * Elimina tutte le attività di un gruppo
     */
    public long deleteByGroupId(Long groupId) {
        return delete("group.id", groupId);
    }

    /**
     * Trova il prossimo display_order disponibile per un gruppo
     */
    public Integer getNextDisplayOrder(Long groupId) {
        Integer max = find("SELECT MAX(a.displayOrder) FROM Activity a WHERE a.group.id = ?1", groupId)
            .project(Integer.class)
            .firstResult();
        return (max == null) ? 0 : max + 1;
    }
}
