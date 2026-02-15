package com.storeapp.itinerary.repository;

import com.storeapp.itinerary.entity.Activity;
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
     * Trova tutte le attività di un itinerario ordinate per display_order
     */
    public List<Activity> findByItineraryId(Long itineraryId) {
        return list("itinerary.id = ?1 ORDER BY displayOrder, scheduledDate, startTime", itineraryId);
    }

    /**
     * Trova attività per itinerario e data
     */
    public List<Activity> findByItineraryIdAndDate(Long itineraryId, LocalDate date) {
        return list("itinerary.id = ?1 AND scheduledDate = ?2 ORDER BY startTime", itineraryId, date);
    }

    /**
     * Trova attività per itinerario e intervallo di date
     */
    public List<Activity> findByItineraryIdAndDateRange(Long itineraryId, LocalDate startDate, LocalDate endDate) {
        return list("itinerary.id = ?1 AND scheduledDate BETWEEN ?2 AND ?3 ORDER BY scheduledDate, startTime",
            itineraryId, startDate, endDate);
    }

    /**
     * Conta attività di un itinerario
     */
    public long countByItineraryId(Long itineraryId) {
        return count("itinerary.id", itineraryId);
    }

    /**
     * Conta attività completate di un itinerario
     */
    public long countCompletedByItineraryId(Long itineraryId) {
        return count("itinerary.id = ?1 AND isCompleted = true", itineraryId);
    }

    /**
     * Elimina tutte le attività di un itinerario
     */
    public long deleteByItineraryId(Long itineraryId) {
        return delete("itinerary.id", itineraryId);
    }

    /**
     * Trova il prossimo display_order disponibile per un itinerario
     */
    public Integer getNextDisplayOrder(Long itineraryId) {
        Integer max = find("SELECT MAX(a.displayOrder) FROM Activity a WHERE a.itinerary.id = ?1", itineraryId)
            .project(Integer.class)
            .firstResult();
        return (max == null) ? 0 : max + 1;
    }
}
