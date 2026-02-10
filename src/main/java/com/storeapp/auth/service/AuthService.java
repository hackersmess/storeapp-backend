package com.storeapp.auth.service;

import com.storeapp.auth.dto.AuthResponse;
import com.storeapp.auth.dto.LoginRequest;
import com.storeapp.auth.dto.RegisterRequest;
import com.storeapp.auth.dto.UserDto;
import com.storeapp.auth.exception.InvalidCredentialsException;
import com.storeapp.auth.exception.UserAlreadyExistsException;
import com.storeapp.user.entity.User;
import com.storeapp.user.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import static jakarta.transaction.Transactional.TxType;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Service per la gestione dell'autenticazione.
 */
@ApplicationScoped
public class AuthService {

    @Inject
    UserRepository userRepository;

    @Inject
    PasswordService passwordService;

    @Inject
    JwtService jwtService;

    @Inject
    EntityManager entityManager;

    /**
     * Registra un nuovo utente.
     *
     * @param request dati di registrazione
     * @return risposta con JWT token e dati utente
     * @throws UserAlreadyExistsException se l'email è già registrata
     */
    @Transactional(TxType.REQUIRES_NEW)
    public AuthResponse register(RegisterRequest request) {
        // Verifica se l'email esiste già
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already registered: " + request.getEmail());
        }

        // Crea nuovo utente
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPasswordHash(passwordService.hashPassword(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Salva nel database
        userRepository.persist(user);
        entityManager.flush(); // Forza il flush per ottenere l'ID generato

        // Genera JWT tokens
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), Set.of("USER"));
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        // Crea risposta
        UserDto userDto = UserDto.fromEntity(user);
        return new AuthResponse(accessToken, refreshToken, userDto);
    }

    /**
     * Esegue il login di un utente.
     *
     * @param request credenziali di login
     * @return risposta con JWT token e dati utente
     * @throws InvalidCredentialsException se le credenziali sono invalide
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Cerca utente per email
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // Verifica password
        if (user.getPasswordHash() == null || 
            !passwordService.verifyPassword(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Aggiorna last login (opzionale)
        user.setUpdatedAt(LocalDateTime.now());

        // Genera JWT tokens
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), Set.of("USER"));
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        // Crea risposta
        UserDto userDto = UserDto.fromEntity(user);
        return new AuthResponse(accessToken, refreshToken, userDto);
    }

    /**
     * Refresh del token JWT.
     *
     * @param refreshToken refresh token
     * @return nuovo access token
     */
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        try {
            var jwt = jwtService.parseToken(refreshToken);
            Long userId = jwtService.getUserIdFromToken(jwt);

            // Verifica che sia un refresh token
            String tokenType = jwt.getClaim("type");
            if (!"refresh".equals(tokenType)) {
                throw new InvalidCredentialsException("Invalid token type");
            }

            // Recupera utente
            User user = userRepository.findUserById(userId);
            if (user == null) {
                throw new InvalidCredentialsException("User not found");
            }

            // Genera nuovo access token
            String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), Set.of("USER"));
            
            UserDto userDto = UserDto.fromEntity(user);
            return new AuthResponse(newAccessToken, refreshToken, userDto);

        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid refresh token");
        }
    }
}
