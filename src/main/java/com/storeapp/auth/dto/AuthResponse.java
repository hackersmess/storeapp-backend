package com.storeapp.auth.dto;

/**
 * DTO per la risposta di autenticazione (login/register).
 * Contiene il JWT token e i dati dell'utente.
 */
public class AuthResponse {

    private String token;
    private String refreshToken;
    private UserDto user;

    // Costruttori
    public AuthResponse() {}

    public AuthResponse(String token, String refreshToken, UserDto user) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    // Getters e Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}
