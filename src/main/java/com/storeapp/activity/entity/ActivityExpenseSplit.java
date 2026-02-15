package com.storeapp.activity.entity;

import com.storeapp.group.entity.GroupMember;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entità per la suddivisione delle spese di un'attività tra i membri
 */
@Entity
@Table(
    name = "activity_expense_splits",
    uniqueConstraints = @UniqueConstraint(columnNames = {"expense_id", "group_member_id"})
)
public class ActivityExpenseSplit extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    public ActivityExpense expense;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_member_id", nullable = false)
    public GroupMember groupMember;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    public BigDecimal amount;

    @Column(name = "is_paid")
    public Boolean isPaid = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
