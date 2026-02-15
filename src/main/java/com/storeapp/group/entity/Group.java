package com.storeapp.group.entity;

import com.storeapp.user.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entità per i gruppi di vacanza
 */
@Entity
@Table(name = "groups")
public class Group extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    public String name;

    @Column(columnDefinition = "TEXT")
    public String description;

    @NotNull(message = "La data di inizio vacanza è obbligatoria")
    @Column(name = "vacation_start_date", nullable = false)
    public LocalDate vacationStartDate;

    @NotNull(message = "La data di fine vacanza è obbligatoria")
    @Column(name = "vacation_end_date", nullable = false)
    public LocalDate vacationEndDate;

    @Size(max = 500)
    @Column(name = "cover_image_url", length = 500)
    public String coverImageUrl;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    public User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public Set<GroupMember> members = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Conta il numero di membri del gruppo
     */
    public long getMemberCount() {
        return members.size();
    }

    /**
     * Verifica se un utente è admin del gruppo
     */
    public boolean isAdmin(Long userId) {
        return members.stream()
            .anyMatch(m -> m.user.getId().equals(userId) && m.role == GroupRole.ADMIN);
    }

    /**
     * Verifica se un utente è membro del gruppo (ADMIN o MEMBER)
     */
    public boolean isMember(Long userId) {
        return members.stream()
            .anyMatch(m -> m.user.getId().equals(userId));
    }

    /**
     * Verifica se un utente è il creatore del gruppo
     */
    public boolean isCreator(Long userId) {
        return createdBy.getId().equals(userId);
    }
}
