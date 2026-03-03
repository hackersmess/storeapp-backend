package com.storeapp.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.storeapp.auth.dto.AuthResponse;
import com.storeapp.auth.dto.GoogleTokenResponse;
import com.storeapp.auth.dto.GoogleUserInfo;
import com.storeapp.auth.mapper.UserDtoMapper;
import com.storeapp.user.entity.User;
import com.storeapp.user.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Service per la gestione dell'autenticazione OAuth2 con Google.
 *
 * Flusso:
 *  1. buildGoogleAuthUrl()    → genera URL di autorizzazione Google con state CSRF
 *  2. exchangeCodeForTokens() → scambia il codice con i token Google
 *  3. getUserInfo()           → recupera le informazioni utente da Google
 *  4. findOrCreateUser()      → trova o crea l'utente nel DB
 *  5. Genera JWT propri e restituisce AuthResponse
 */
@ApplicationScoped
public class OAuthService {

    @ConfigProperty(name = "storeapp.oauth2.google.client-id")
    String googleClientId;

    @ConfigProperty(name = "storeapp.oauth2.google.client-secret")
    String googleClientSecret;

    @ConfigProperty(name = "storeapp.oauth2.google.redirect-uri")
    String googleRedirectUri;

    @ConfigProperty(name = "storeapp.oauth2.google.auth-uri",
            defaultValue = "https://accounts.google.com/o/oauth2/v2/auth")
    String googleAuthUri;

    @ConfigProperty(name = "storeapp.oauth2.google.token-uri",
            defaultValue = "https://oauth2.googleapis.com/token")
    String googleTokenUri;

    @ConfigProperty(name = "storeapp.oauth2.google.userinfo-uri",
            defaultValue = "https://www.googleapis.com/oauth2/v3/userinfo")
    String googleUserinfoUri;

    @Inject
    UserRepository userRepository;

    @Inject
    JwtService jwtService;

    @Inject
    UserDtoMapper userDtoMapper;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // -------------------------------------------------------------------------
    // 1. Costruisce l'URL di autorizzazione Google
    // -------------------------------------------------------------------------

    /**
     * Costruisce l'URL a cui reindirizzare l'utente per il login Google.
     *
     * @param state valore CSRF (opaco) da restituire nel callback
     * @return URL completo di autorizzazione Google
     */
    public String buildGoogleAuthUrl(String state) {
        return googleAuthUri
                + "?client_id=" + encode(googleClientId)
                + "&redirect_uri=" + encode(googleRedirectUri)
                + "&response_type=code"
                + "&scope=" + encode("openid email profile")
                + "&access_type=offline"
                + "&prompt=consent"
                + "&state=" + encode(state);
    }

    /**
     * Genera un valore CSRF casuale sicuro (base64url, 32 byte).
     */
    public String generateState() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // -------------------------------------------------------------------------
    // 2. Scambia il codice con i token Google
    // -------------------------------------------------------------------------

    /**
     * Chiama il token endpoint di Google e restituisce i token.
     *
     * @param code codice di autorizzazione ricevuto nel callback
     * @return GoogleTokenResponse con access_token, id_token, ecc.
     */
    public GoogleTokenResponse exchangeCodeForTokens(String code) throws IOException, InterruptedException {
        String requestBody = new StringJoiner("&")
                .add("grant_type=" + encode("authorization_code"))
                .add("code=" + encode(code))
                .add("redirect_uri=" + encode(googleRedirectUri))
                .add("client_id=" + encode(googleClientId))
                .add("client_secret=" + encode(googleClientSecret))
                .toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(googleTokenUri))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Errore dal token endpoint di Google: HTTP " + response.statusCode()
                    + " - " + response.body());
        }

        return objectMapper.readValue(response.body(), GoogleTokenResponse.class);
    }

    // -------------------------------------------------------------------------
    // 3. Recupera le informazioni utente da Google
    // -------------------------------------------------------------------------

    /**
     * Chiama il userinfo endpoint di Google usando l'access token.
     *
     * @param accessToken access token Google
     * @return GoogleUserInfo con email, nome, picture, ecc.
     */
    public GoogleUserInfo getUserInfo(String accessToken) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(googleUserinfoUri))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Errore dal userinfo endpoint di Google: HTTP " + response.statusCode()
                    + " - " + response.body());
        }

        return objectMapper.readValue(response.body(), GoogleUserInfo.class);
    }

    // -------------------------------------------------------------------------
    // 4. Trova o crea l'utente nel database
    // -------------------------------------------------------------------------

    /**
     * Cerca un utente per Google ID; se non esiste, lo cerca per email;
     * se non esiste ancora, lo crea. Aggiorna il googleId se mancante.
     *
     * @param userInfo informazioni provenienti da Google
     * @return User entity salvata nel database
     */
    @Transactional
    public User findOrCreateUser(GoogleUserInfo userInfo) {
        // Prima cerca per Google ID
        Optional<User> byGoogleId = userRepository.findByGoogleId(userInfo.getSub());
        if (byGoogleId.isPresent()) {
            User user = byGoogleId.get();
            // Aggiorna avatar se cambiato
            if (userInfo.getPicture() != null && !userInfo.getPicture().equals(user.getAvatarUrl())) {
                user.setAvatarUrl(userInfo.getPicture());
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.merge(user);
            }
            return user;
        }

        // Poi cerca per email (utente già registrato con email/password)
        Optional<User> byEmail = userRepository.findByEmail(userInfo.getEmail());
        if (byEmail.isPresent()) {
            User user = byEmail.get();
            // Collega il Google ID all'account esistente
            user.setGoogleId(userInfo.getSub());
            if (user.getAvatarUrl() == null && userInfo.getPicture() != null) {
                user.setAvatarUrl(userInfo.getPicture());
            }
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.merge(user);
        }

        // Crea un nuovo utente OAuth
        User newUser = new User();
        newUser.setEmail(userInfo.getEmail());
        newUser.setName(userInfo.getName() != null ? userInfo.getName() : userInfo.getEmail());
        newUser.setGoogleId(userInfo.getSub());
        newUser.setAvatarUrl(userInfo.getPicture());
        newUser.setPasswordHash(null); // Nessuna password per utenti OAuth
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        userRepository.persist(newUser);
        return newUser;
    }

    // -------------------------------------------------------------------------
    // 5. Genera AuthResponse con JWT propri
    // -------------------------------------------------------------------------

    /**
     * Esegue l'intero flusso OAuth: code → tokens → userinfo → user → JWT.
     *
     * @param code codice di autorizzazione ricevuto dal callback Google
     * @return AuthResponse con i nostri JWT e i dati utente
     */
    public AuthResponse handleOAuthCallback(String code) throws IOException, InterruptedException {
        // Scambia il codice con i token Google
        GoogleTokenResponse googleTokens = exchangeCodeForTokens(code);

        // Recupera le informazioni utente
        GoogleUserInfo userInfo = getUserInfo(googleTokens.getAccessToken());

        // Trova o crea l'utente nel DB
        User user = findOrCreateUser(userInfo);

        // Genera i nostri JWT
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), Set.of("USER"));
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        return new AuthResponse(accessToken, refreshToken, userDtoMapper.toDto(user));
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
