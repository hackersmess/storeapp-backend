package com.storeapp.auth.controller;

import com.storeapp.auth.dto.*;
import com.storeapp.auth.exception.AuthException;
import com.storeapp.auth.exception.InvalidCredentialsException;
import com.storeapp.auth.exception.UserAlreadyExistsException;
import com.storeapp.auth.service.AuthService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

/**
 * Controller REST per gestire le operazioni di autenticazione.
 * 
 * Endpoint base: /api/auth
 */
@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    AuthService authService;

    /**
     * POST /api/auth/register
     * Registra un nuovo utente.
     *
     * @param request dati di registrazione
     * @return 201 Created con JWT token e dati utente
     */
    @POST
    @Path("/register")
    public Response register(@Valid RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (UserAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Registration failed"))
                .build();
        }
    }

    /**
     * POST /api/auth/login
     * Esegue il login di un utente.
     *
     * @param request credenziali di login
     * @return 200 OK con JWT token e dati utente
     */
    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return Response.ok(response).build();
        } catch (InvalidCredentialsException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Login failed"))
                .build();
        }
    }

    /**
     * POST /api/auth/refresh
     * Rinnova il token JWT usando un refresh token.
     *
     * @param request refresh token
     * @return 200 OK con nuovo access token
     */
    @POST
    @Path("/refresh")
    public Response refreshToken(@Valid RefreshTokenRequest request) {
        try {
            AuthResponse response = authService.refreshToken(request.getRefreshToken());
            return Response.ok(response).build();
        } catch (InvalidCredentialsException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                .entity(Map.of("error", "Invalid refresh token"))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Token refresh failed"))
                .build();
        }
    }

    /**
     * GET /api/auth/health
     * Verifica che il servizio di autenticazione sia attivo.
     *
     * @return 200 OK
     */
    @GET
    @Path("/health")
    public Response health() {
        return Response.ok(Map.of("status", "Auth service is running")).build();
    }
}
