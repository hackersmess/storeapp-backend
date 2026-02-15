package com.storeapp.itinerary.entity;

import com.storeapp.group.entity.Group;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Entità per le attività di un gruppo
 * Collegata direttamente al gruppo (non più tramite itinerario)
 * Supporta diversi provider di geolocalizzazione (Mapbox, Google Maps, ecc.)
 */
@Entity
@Table(name = "activities")
public class Activity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    public Group group;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    public String name;

    @Column(columnDefinition = "TEXT")
    public String description;

    @Column(name = "scheduled_date")
    public LocalDate scheduledDate;

    @Column(name = "start_time")
    public LocalTime startTime;

    @Column(name = "end_time")
    public LocalTime endTime;

    // Campi geolocalizzazione (provider-agnostic)
    @Size(max = 200)
    @Column(name = "location_name", length = 200)
    public String locationName;

    @Size(max = 500)
    @Column(name = "location_address", length = 500)
    public String locationAddress;

    @Column(name = "location_lat", precision = 10, scale = 7)
    public BigDecimal locationLat;

    @Column(name = "location_lng", precision = 10, scale = 7)
    public BigDecimal locationLng;

    @Size(max = 500)
    @Column(name = "location_place_id", length = 500)
    public String locationPlaceId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "location_provider", nullable = false, length = 50)
    public LocationProvider locationProvider = LocationProvider.MAPBOX;

    @Type(JsonBinaryType.class)
    @Column(name = "location_metadata", columnDefinition = "jsonb")
    public Map<String, Object> locationMetadata;

    @Column(name = "is_completed")
    public Boolean isCompleted = false;

    @Column(name = "display_order")
    public Integer displayOrder = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public Set<ActivityParticipant> participants = new HashSet<>();

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public Set<ActivityExpense> expenses = new HashSet<>();

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
     * Conta i partecipanti confermati
     */
    public long getConfirmedCount() {
        return participants.stream()
            .filter(p -> p.status == ParticipantStatus.CONFIRMED)
            .count();
    }

    /**
     * Conta i partecipanti incerti
     */
    public long getMaybeCount() {
        return participants.stream()
            .filter(p -> p.status == ParticipantStatus.MAYBE)
            .count();
    }

    /**
     * Conta i partecipanti che hanno declinato
     */
    public long getDeclinedCount() {
        return participants.stream()
            .filter(p -> p.status == ParticipantStatus.DECLINED)
            .count();
    }
}
