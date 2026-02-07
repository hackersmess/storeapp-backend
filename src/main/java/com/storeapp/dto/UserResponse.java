package com.storeapp.dto;

import java.time.LocalDateTime;

/**
 * DTO per la risposta delle API (GET).
 * NON include la password per sicurezza.
 */
public class UserResponse {
    public Long id;
    public String email;
    public String name;
    public String avatarUrl;
    public String bio;
    public String googleId;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    // Costruttore vuoto per Jackson
    public UserResponse() {
    }
}

