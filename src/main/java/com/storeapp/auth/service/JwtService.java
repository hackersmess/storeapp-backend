package com.storeapp.auth.service;

import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

/**
 * Service per la generazione e validazione di JWT token.
 */
@ApplicationScoped
public class JwtService {

    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "storeapp")
    String issuer;

    @Inject
    JWTParser jwtParser;

    private static final Duration ACCESS_TOKEN_VALIDITY = Duration.ofHours(1);
    private static final Duration REFRESH_TOKEN_VALIDITY = Duration.ofDays(30);

    /**
     * Genera un access token JWT.
     *
     * @param userId ID dell'utente
     * @param email email dell'utente
     * @param roles ruoli dell'utente
     * @return JWT token
     */
    public String generateAccessToken(Long userId, String email, Set<String> roles) {
        Instant now = Instant.now();
        
        return Jwt.issuer(issuer)
            .subject(userId.toString())
            .upn(email)
            .groups(roles)
            .issuedAt(now)
            .expiresAt(now.plus(ACCESS_TOKEN_VALIDITY))
            .sign();
    }

    /**
     * Genera un refresh token JWT (durata più lunga).
     *
     * @param userId ID dell'utente
     * @param email email dell'utente
     * @return JWT refresh token
     */
    public String generateRefreshToken(Long userId, String email) {
        Instant now = Instant.now();
        
        return Jwt.issuer(issuer)
            .subject(userId.toString())
            .upn(email)
            .claim("type", "refresh")
            .issuedAt(now)
            .expiresAt(now.plus(REFRESH_TOKEN_VALIDITY))
            .sign();
    }

    /**
     * Valida e parsifica un token JWT.
     *
     * @param token JWT token
     * @return JsonWebToken se valido
     * @throws ParseException se il token è invalido
     */
    public JsonWebToken parseToken(String token) throws ParseException {
        return jwtParser.parse(token);
    }

    /**
     * Estrae l'user ID dal token JWT.
     *
     * @param jwt JsonWebToken
     * @return user ID
     */
    public Long getUserIdFromToken(JsonWebToken jwt) {
        return Long.parseLong(jwt.getSubject());
    }
}
