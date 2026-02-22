package com.storeapp.activity.entity;

import com.storeapp.group.entity.Group;
import com.storeapp.user.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Base abstract class for all activities using JOINED inheritance strategy
 * 
 * Inheritance hierarchy:
 * - Activity (abstract base class) 
 *   ├── Event (single-location activities: restaurants, museums, hotels)
 *   └── Trip (travel activities with origin→destination: flights, trains)
 * 
 * Multi-day support: use start_date + end_date (e.g., hotel stays, multi-day events)
 * Location provider: configured globally in application.properties
 */
@Entity
@Table(name = "activities")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "activity_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Activity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    public Group group;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    public String name;

    @Column(columnDefinition = "TEXT")
    public String description;

    // Multi-day support
    @NotNull(message = "La data di inizio è obbligatoria")
    @Column(name = "start_date", nullable = false)
    public LocalDate startDate;

    @NotNull(message = "La data di fine è obbligatoria")
    @Column(name = "end_date", nullable = false)
    public LocalDate endDate;

    @NotNull(message = "L'ora di inizio è obbligatoria")
    @Column(name = "start_time", nullable = false)
    public LocalTime startTime;

    @NotNull(message = "L'ora di fine è obbligatoria")
    @Column(name = "end_time", nullable = false)
    public LocalTime endTime;

    // Activity status
    @Column(name = "is_completed")
    public Boolean isCompleted = false;

    @Column(name = "display_order")
    public Integer displayOrder = 0;

    @Column(name = "total_cost", precision = 10, scale = 2)
    public BigDecimal totalCost = BigDecimal.ZERO;

    // Audit fields
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    public User createdBy;

    // Relationships (per ora non implementiamo expenses)
    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public Set<ActivityParticipant> participants = new HashSet<>();

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        validateDatesAndTimes();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        validateDatesAndTimes();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Validate that end date/time are after start date/time
     */
    private void validateDatesAndTimes() {
        if (startDate != null && endDate != null && startTime != null && endTime != null) {
            LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
            LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);
            
            if (!endDateTime.isAfter(startDateTime)) {
                throw new IllegalArgumentException(
                    "Data e ora di fine devono essere successive a quelle di inizio"
                );
            }
        }
    }

    // Business methods
    /**
     * Check if this is a multi-day activity
     */
    public boolean isMultiDay() {
        return endDate != null && startDate != null && endDate.isAfter(startDate);
    }

    /**
     * Get duration in days (inclusive)
     */
    public int getDurationDays() {
        if (startDate == null) {
            return 0;
        }
        if (endDate == null || endDate.equals(startDate)) {
            return 1;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    /**
     * Count confirmed participants
     */
    public long getConfirmedCount() {
        return participants.stream()
            .filter(p -> p.status == ParticipantStatus.CONFIRMED)
            .count();
    }

    /**
     * Count maybe participants
     */
    public long getMaybeCount() {
        return participants.stream()
            .filter(p -> p.status == ParticipantStatus.MAYBE)
            .count();
    }

    /**
     * Count declined participants
     */
    public long getDeclinedCount() {
        return participants.stream()
            .filter(p -> p.status == ParticipantStatus.DECLINED)
            .count();
    }

    /**
     * Get activity type (EVENT or TRIP) - to be implemented by subclasses
     */
    public abstract String getActivityType();
}
