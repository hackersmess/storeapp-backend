package com.storeapp.auth.dto;

import com.storeapp.user.entity.User;

import java.time.LocalDateTime;

/**
 * DTO che rappresenta un utente nelle risposte API.
 * Non espone informazioni sensibili come password_hash.
 */
public class UserDto {

    private Long id;
    private String email;
    private String name;
    private String avatarUrl;
    private String bio;
    private LocalDateTime createdAt;

    // Costruttori
    public UserDto() {}

    public UserDto(Long id, String email, String name, String avatarUrl, String bio, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.createdAt = createdAt;
    }

    /**
     * Factory method per creare UserDto da entità User.
     */
    public static UserDto fromEntity(User user) {
        return new UserDto(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getAvatarUrl(),
            user.getBio(),
            user.getCreatedAt()
        );
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
