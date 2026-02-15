package com.storeapp.itinerary.repository;

import com.storeapp.itinerary.entity.Itinerary;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

/**
 * Repository per gli itinerari
 */
@ApplicationScoped
public class ItineraryRepository implements PanacheRepository<Itinerary> {

    /**
     * Trova un itinerario per ID gruppo
     */
    public Optional<Itinerary> findByGroupId(Long groupId) {
        return find("group.id", groupId).firstResultOptional();
    }

    /**
     * Verifica se esiste un itinerario per un gruppo
     */
    public boolean existsByGroupId(Long groupId) {
        return count("group.id", groupId) > 0;
    }

    /**
     * Elimina un itinerario per ID gruppo
     */
    public boolean deleteByGroupId(Long groupId) {
        return delete("group.id", groupId) > 0;
    }
}
