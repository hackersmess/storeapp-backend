package com.storeapp.user.repository;

import com.storeapp.user.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

/**
 * Repository per l'accesso ai dati della tabella "users".
 * Gestisce solo le operazioni CRUD sul database.
 * Non contiene logica business.
 */
@ApplicationScoped
public class UserRepository {

    @PersistenceContext
    EntityManager em;

    /**
     * Trova un utente per ID.
     */
    public Optional<User> findById(Long id) {
        User user = em.find(User.class, id);
        return Optional.ofNullable(user);
    }

    /**
     * Trova un utente per ID (restituisce direttamente User o null).
     */
    public User findUserById(Long id) {
        return em.find(User.class, id);
    }

    /**
     * Trova un utente per email.
     */
    public Optional<User> findByEmail(String email) {
        try {
            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Trova un utente per nome.
     */
    public Optional<User> findByName(String name) {
        try {
            User user = em.createQuery("SELECT u FROM User u WHERE u.name = :name", User.class)
                    .setParameter("name", name)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Trova tutti gli utenti.
     */
    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    /**
     * Cerca utenti per nome o email (case-insensitive), escludendo certi ID.
     * Query ottimizzata: filtra tutto a livello database.
     * 
     * @param searchQuery Query di ricerca (cerca in email e nome)
     * @param excludeIds Lista di ID utenti da escludere (es. membri già presenti)
     * @return Lista di utenti che corrispondono alla ricerca
     */
    public List<User> searchUsersExcluding(String searchQuery, List<Long> excludeIds) {
        String jpql = "SELECT u FROM User u WHERE " +
                      "LOWER(u.email) LIKE LOWER(:search) OR LOWER(u.name) LIKE LOWER(:search)";
        
        // Se ci sono ID da escludere, aggiungi la condizione
        if (excludeIds != null && !excludeIds.isEmpty()) {
            jpql += " AND u.id NOT IN :excludeIds";
        }
        
        var query = em.createQuery(jpql, User.class)
                     .setParameter("search", "%" + searchQuery + "%");
        
        if (excludeIds != null && !excludeIds.isEmpty()) {
            query.setParameter("excludeIds", excludeIds);
        }
        
        return query.getResultList();
    }

    /**
     * Trova utenti non presenti in una lista di ID.
     * 
     * @param excludeIds Lista di ID utenti da escludere
     * @return Lista di utenti non presenti nella lista di esclusione
     */
    public List<User> findAllExcluding(List<Long> excludeIds) {
        if (excludeIds == null || excludeIds.isEmpty()) {
            return findAll();
        }
        
        return em.createQuery("SELECT u FROM User u WHERE u.id NOT IN :excludeIds", User.class)
                 .setParameter("excludeIds", excludeIds)
                 .getResultList();
    }

    /**
     * Conta il numero totale di utenti.
     */
    public long count() {
        return em.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
    }

    /**
     * Verifica se esiste un utente con la data email.
     */
    public boolean existsByEmail(String email) {
        Long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    /**
     * Persiste un nuovo utente nel database.
     */
    public void persist(User user) {
        em.persist(user);
    }

    /**
     * Aggiorna un utente esistente.
     */
    public User merge(User user) {
        return em.merge(user);
    }

    /**
     * Elimina un utente dal database.
     */
    public void delete(User user) {
        // Se l'entità non è managed, la riattachiamo
        if (!em.contains(user)) {
            user = em.merge(user);
        }
        em.remove(user);
    }
}
