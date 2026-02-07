package com.storeapp.service;

import com.storeapp.dto.CreateUserRequest;
import com.storeapp.dto.UpdateUserRequest;
import com.storeapp.dto.UserResponse;
import com.storeapp.entity.User;
import com.storeapp.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Business Service per la gestione degli utenti.
 * Contiene tutta la logica business:
 * - Validazioni
 * - Hashing password
 * - Conversioni Entity <-> DTO
 * - Regole di business
 */
@ApplicationScoped
public class UserBusinessService {

    @Inject
    UserRepository userRepository;

    /**
     * Recupera tutti gli utenti.
     * @return lista di UserResponse (senza password)
     */
    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Recupera un utente per ID.
     * @throws NotFoundException se l'utente non esiste
     */
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utente con id " + id + " non trovato"));
        return toResponse(user);
    }

    /**
     * Recupera un utente per email.
     * @throws NotFoundException se l'utente non esiste
     */
    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Utente con email " + email + " non trovato"));
        return toResponse(user);
    }

    /**
     * Crea un nuovo utente.
     * @throws WebApplicationException se l'email esiste già
     */
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        // Validazione business: email univoca
        if (userRepository.existsByEmail(request.email)) {
            throw new WebApplicationException("Email già utilizzata", 409);
        }

        // Creazione entity
        User user = new User();
        user.setEmail(request.email);
        user.setName(request.name);
        user.setPasswordHash(hashPassword(request.password));
        user.setAvatarUrl(request.avatarUrl);
        user.setBio(request.bio);
        user.setGoogleId(request.googleId);

        // Persist nel DB
        userRepository.persist(user);

        return toResponse(user);
    }

    /**
     * Aggiorna un utente esistente.
     * @throws NotFoundException se l'utente non esiste
     * @throws WebApplicationException se la nuova email esiste già
     */
    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utente con id " + id + " non trovato"));

        // Aggiorna solo i campi forniti (partial update)
        if (request.email != null && !request.email.equals(user.getEmail())) {
            // Validazione business: email univoca
            if (userRepository.existsByEmail(request.email)) {
                throw new WebApplicationException("Email già utilizzata", 409);
            }
            user.setEmail(request.email);
        }

        if (request.name != null) {
            user.setName(request.name);
        }

        if (request.password != null) {
            user.setPasswordHash(hashPassword(request.password));
        }

        if (request.avatarUrl != null) {
            user.setAvatarUrl(request.avatarUrl);
        }

        if (request.bio != null) {
            user.setBio(request.bio);
        }

        if (request.googleId != null) {
            user.setGoogleId(request.googleId);
        }

        // Update nel DB
        user = userRepository.merge(user);

        return toResponse(user);
    }

    /**
     * Elimina un utente.
     * @throws NotFoundException se l'utente non esiste
     */
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utente con id " + id + " non trovato"));
        userRepository.delete(user);
    }

    /**
     * Conta il numero totale di utenti.
     */
    public long count() {
        return userRepository.count();
    }

    /**
     * Verifica se una password è corretta per un dato utente.
     * @return true se la password è corretta
     */
    public boolean verifyPassword(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Utente con email " + email + " non trovato"));
        return BCrypt.checkpw(password, user.getPasswordHash());
    }

    // ========== METODI PRIVATI DI UTILITÀ ==========

    /**
     * Hash della password con BCrypt.
     */
    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * Converte Entity -> DTO Response (nasconde la password).
     */
    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.id = user.getId();
        response.email = user.getEmail();
        response.name = user.getName();
        response.avatarUrl = user.getAvatarUrl();
        response.bio = user.getBio();
        response.googleId = user.getGoogleId();
        response.createdAt = user.getCreatedAt();
        response.updatedAt = user.getUpdatedAt();
        return response;
    }
}
