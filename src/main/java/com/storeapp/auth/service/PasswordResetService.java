package com.storeapp.auth.service;

import com.storeapp.user.entity.User;
import com.storeapp.user.repository.UserRepository;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Service che gestisce il flusso di reset password via email.
 */
@ApplicationScoped
public class PasswordResetService {

    private static final Logger LOG = Logger.getLogger(PasswordResetService.class);
    private static final int TOKEN_TTL_MINUTES = 30;

    @Inject
    UserRepository userRepository;

    @Inject
    PasswordService passwordService;

    @Inject
    Mailer mailer;

    @ConfigProperty(name = "storeapp.app.frontend-url", defaultValue = "http://localhost:4200")
    String frontendUrl;

    @ConfigProperty(name = "quarkus.mailer.mock", defaultValue = "true")
    boolean mailerMock;

    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email).orElse(null);

        // Risposta sempre identica per non rivelare se un'email esiste o meno.
        if (user == null) {
            return;
        }

        String rawToken = generateSecureToken();
        String tokenHash = hashToken(rawToken);

        user.setPasswordResetTokenHash(tokenHash);
        user.setPasswordResetTokenExpiresAt(LocalDateTime.now().plusMinutes(TOKEN_TTL_MINUTES));
        userRepository.merge(user);

        sendResetEmail(user.getEmail(), user.getName(), rawToken);
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        String tokenHash = hashToken(rawToken);
        LocalDateTime now = LocalDateTime.now();

        User user = userRepository.findByValidPasswordResetTokenHash(tokenHash, now)
                .orElseThrow(() -> new IllegalArgumentException("Token non valido o scaduto"));

        user.setPasswordHash(passwordService.hashPassword(newPassword));
        user.setPasswordResetTokenHash(null);
        user.setPasswordResetTokenExpiresAt(null);
        user.setUpdatedAt(now);
        userRepository.merge(user);
    }

    private void sendResetEmail(String email, String name, String rawToken) {
        String encodedToken = URLEncoder.encode(rawToken, StandardCharsets.UTF_8);
        String resetLink = frontendUrl + "/reset-password?token=" + encodedToken;

        if (mailerMock) {
            LOG.infof("Mailer mock attivo, reset link per %s: %s", email, resetLink);
        }

        String body = "Ciao " + (name != null ? name : "utente") + ",\n\n"
                + "Abbiamo ricevuto una richiesta di reset password per il tuo account StoreApp.\n\n"
                + "Clicca qui per impostare una nuova password:\n"
                + resetLink + "\n\n"
                + "Il link scade tra " + TOKEN_TTL_MINUTES + " minuti.\n"
                + "Se non hai richiesto tu il reset, ignora questa email.\n\n"
                + "StoreApp Team";

        try {
            mailer.send(Mail.withText(email, "StoreApp - Reset Password", body));
        } catch (Exception e) {
            LOG.errorf(e, "Errore invio email reset password verso %s", email);
            throw new RuntimeException("Impossibile inviare email di reset password", e);
        }
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hashed.length * 2);
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Errore nella generazione hash token", e);
        }
    }
}
