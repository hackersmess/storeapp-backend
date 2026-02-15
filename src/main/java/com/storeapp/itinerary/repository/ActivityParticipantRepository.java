package com.storeapp.itinerary.repository;

import com.storeapp.itinerary.entity.ActivityParticipant;
import com.storeapp.itinerary.entity.ParticipantStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

/**
 * Repository per i partecipanti alle attività
 */
@ApplicationScoped
public class ActivityParticipantRepository implements PanacheRepository<ActivityParticipant> {

    /**
     * Trova tutti i partecipanti di un'attività
     */
    public List<ActivityParticipant> findByActivityId(Long activityId) {
        return list("activity.id = ?1", activityId);
    }

    /**
     * Trova un partecipante specifico
     */
    public Optional<ActivityParticipant> findByActivityIdAndGroupMemberId(Long activityId, Long groupMemberId) {
        return find("activity.id = ?1 AND groupMember.id = ?2", activityId, groupMemberId).firstResultOptional();
    }

    /**
     * Conta partecipanti per status
     */
    public long countByActivityIdAndStatus(Long activityId, ParticipantStatus status) {
        return count("activity.id = ?1 AND status = ?2", activityId, status);
    }

    /**
     * Elimina tutti i partecipanti di un'attività
     */
    public long deleteByActivityId(Long activityId) {
        return delete("activity.id", activityId);
    }

    /**
     * Verifica se un membro è già partecipante
     */
    public boolean existsByActivityIdAndGroupMemberId(Long activityId, Long groupMemberId) {
        return count("activity.id = ?1 AND groupMember.id = ?2", activityId, groupMemberId) > 0;
    }
}
