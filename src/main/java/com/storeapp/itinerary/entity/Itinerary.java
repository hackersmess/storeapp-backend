package com.storeapp.itinerary.entity;

import com.storeapp.group.entity.Group;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entità per gli itinerari di viaggio
 * Relazione 1:1 con Group (un gruppo ha un solo itinerario)
 */
@Entity
@Table(name = "itineraries")
public class Itinerary extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false, unique = true)
    public Group group;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    public String name;

    @Column(columnDefinition = "TEXT")
    public String description;

    // Le date sono gestite dal gruppo (group.vacationStartDate, group.vacationEndDate)
    // Non servono più qui per evitare ridondanza

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;

    @OneToMany(mappedBy = "itinerary", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public Set<Activity> activities = new HashSet<>();

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
     * Conta il numero di attività nell'itinerario
     */
    public long getActivityCount() {
        return activities.size();
    }

    /**
     * Conta le attività completate
     */
    public long getCompletedActivitiesCount() {
        return activities.stream()
            .filter(a -> Boolean.TRUE.equals(a.isCompleted))
            .count();
    }
}
