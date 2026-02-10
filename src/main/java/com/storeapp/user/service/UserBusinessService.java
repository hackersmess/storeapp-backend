package com.storeapp.user.service;

import com.storeapp.user.dto.CreateUserRequest;
import com.storeapp.user.dto.UpdateUserRequest;
import com.storeapp.user.dto.UserResponse;
import com.storeapp.user.entity.User;
import com.storeapp.user.mapper.UserMapper;
import com.storeapp.user.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

/**
 * Business Service per la gestione degli utenti.
 * Contiene tutta la logica business:
 * - Validazioni
 * - Hashing password
 * - Regole di business
 *
 * Le conversioni Entity <-> DTO sono delegate a UserMapper
 */
@ApplicationScoped
public class UserBusinessService {

    @Inject
    UserRepository userRepository;

    @Inject
    UserMapper userMapper;

    /**
     * Recupera tutti gli utenti.
     * @return lista di UserResponse (senza password)
     */
    public List<UserResponse> findAll() {
        List<User> users = userRepository.findAll();
        return userMapper.toUserResponseList(users);
    }

    /**
     * Recupera un utente per ID.
     * @throws NotFoundException se l'utente non esiste
     */
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utente con id " + id + " non trovato"));
        return userMapper.toUserResponse(user);
    }

    /**
     * Recupera un utente per email.
     * @throws NotFoundException se l'utente non esiste
     */
    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Utente con email " + email + " non trovato"));
        return userMapper.toUserResponse(user);
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

        // Creazione entity usando mapper
        User user = userMapper.toEntity(request);
        user.setPasswordHash(hashPassword(request.password));

        // Persist nel DB
        userRepository.persist(user);

        return userMapper.toUserResponse(user);
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

        // Validazione business: email univoca
        if (request.email != null && !request.email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.email)) {
                throw new WebApplicationException("Email già utilizzata", 409);
            }
        }

        // Aggiorna entity usando mapper (partial update)
        userMapper.updateEntityFromRequest(request, user);

        // Gestisce password separatamente (hashing)
        if (request.password != null) {
            user.setPasswordHash(hashPassword(request.password));
        }

        // Update nel DB
        user = userRepository.merge(user);

        return userMapper.toUserResponse(user);
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
}
