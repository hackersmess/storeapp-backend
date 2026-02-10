package com.storeapp.group.repository;

import com.storeapp.group.entity.Group;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

/**
 * Repository per i gruppi
 */
@ApplicationScoped
public class GroupRepository implements PanacheRepository<Group> {

    /**
     * Trova tutti i gruppi creati da un utente
     */
    public List<Group> findByCreator(Long userId) {
        return find("createdBy.id", userId).list();
    }

    /**
     * Trova tutti i gruppi di cui un utente è membro
     */
    public List<Group> findByMember(Long userId) {
        return find("SELECT DISTINCT g FROM Group g JOIN g.members m WHERE m.user.id = ?1", userId).list();
    }

    /**
     * Trova un gruppo per ID con membri caricati
     */
    public Optional<Group> findByIdWithMembers(Long id) {
        return find("SELECT g FROM Group g LEFT JOIN FETCH g.members m LEFT JOIN FETCH m.user WHERE g.id = ?1", id)
            .firstResultOptional();
    }

    /**
     * Cerca gruppi per nome (case-insensitive)
     */
    public List<Group> searchByName(String searchTerm) {
        return find("LOWER(name) LIKE LOWER(?1)", "%" + searchTerm + "%").list();
    }
}
