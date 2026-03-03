package com.storeapp.auth.controller;

import com.storeapp.auth.dto.AuthResponse;
import com.storeapp.auth.service.OAuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Controller REST per l'autenticazione OAuth2 con Google.
 *
 * Endpoint:
 *   GET /api/auth/oauth2/google   → reindirizza l'utente a Google
 *   GET /api/auth/oauth2/callback → riceve il codice, genera JWT, reindirizza al frontend
 */
@Path("/api/auth/oauth2")
@Produces(MediaType.APPLICATION_JSON)
public class OAuthController {

    @Inject
    OAuthService oAuthService;

    @ConfigProperty(name = "storeapp.app.frontend-url", defaultValue = "http://localhost:4200")
    String frontendUrl;

    /**
     * GET /api/auth/oauth2/google
     *
     * Reindirizza il browser dell'utente alla pagina di login di Google.
     * Genera un state CSRF e lo memorizza in un cookie HttpOnly di breve durata.
     */
    @GET
    @Path("/google")
    public Response initiateGoogleLogin() {
        String state = oAuthService.generateState();

        // Salva lo state in un cookie HttpOnly per validarlo nel callback
        NewCookie stateCookie = new NewCookie.Builder("oauth_state")
                .value(state)
                .path("/api/auth/oauth2")
                .maxAge(300) // 5 minuti
                .httpOnly(true)
                .build();

        String authUrl = oAuthService.buildGoogleAuthUrl(state);

        return Response.temporaryRedirect(URI.create(authUrl))
                .cookie(stateCookie)
                .build();
    }

    /**
     * GET /api/auth/oauth2/callback?code=...&state=...
     *
     * Google reindirizza qui dopo che l'utente ha autorizzato l'app.
     * Scambia il codice con i token, crea/aggiorna l'utente nel DB,
     * genera i JWT propri e reindirizza al frontend con i token come
     * parametri di query (letti una volta sola dal componente Angular).
     */
    @GET
    @Path("/callback")
    public Response handleGoogleCallback(
            @QueryParam("code") String code,
            @QueryParam("state") String state,
            @QueryParam("error") String error,
            @CookieParam("oauth_state") String cookieState) {

        // Gestisci l'errore se l'utente ha negato il consenso
        if (error != null) {
            return redirectToFrontendWithError("Accesso con Google negato: " + error);
        }

        // Valida che code sia presente
        if (code == null || code.isBlank()) {
            return redirectToFrontendWithError("Codice di autorizzazione mancante");
        }

        // Valida il state CSRF
        if (cookieState == null || !cookieState.equals(state)) {
            return redirectToFrontendWithError("Validazione CSRF fallita, riprova");
        }

        try {
            AuthResponse authResponse = oAuthService.handleOAuthCallback(code);

            // Costruisce l'URL di redirect al frontend con i token
            String redirectUrl = frontendUrl + "/oauth-callback"
                    + "?token=" + encode(authResponse.getToken())
                    + "&refreshToken=" + encode(authResponse.getRefreshToken())
                    + "&user=" + encode(serializeUser(authResponse));

            // Cancella il cookie di state
            NewCookie clearStateCookie = new NewCookie.Builder("oauth_state")
                    .value("")
                    .path("/api/auth/oauth2")
                    .maxAge(0)
                    .build();

            return Response.temporaryRedirect(URI.create(redirectUrl))
                    .cookie(clearStateCookie)
                    .build();

        } catch (Exception e) {
            System.err.println("OAuth callback error: " + e.getMessage());
            e.printStackTrace();
            return redirectToFrontendWithError("Errore durante il login con Google");
        }
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    private Response redirectToFrontendWithError(String message) {
        String redirectUrl = frontendUrl + "/oauth-callback"
                + "?error=" + encode(message);
        return Response.temporaryRedirect(URI.create(redirectUrl)).build();
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String serializeUser(AuthResponse response) {
        try {
            com.storeapp.auth.dto.UserDto user = response.getUser();
            // Serializzazione manuale minimale per evitare dipendenza da Jackson nel controller
            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValueAsString(user);
        } catch (Exception e) {
            return "{}";
        }
    }
}
