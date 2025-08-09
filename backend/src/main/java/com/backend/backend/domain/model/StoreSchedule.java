package com.backend.backend.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "store_schedule")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreSchedule {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "day_of_week", nullable = false)
    private Short dayOfWeek;

    @Column(name = "is_closed", nullable = false)
    private Boolean isClosed;

    @Column(name = "lunch_start")
    private Time lunchStart;

    @Column(name = "lunch_end")
    private Time lunchEnd;

    @Column(name = "is_lunch_closed")
    private Boolean isLunchClosed;

    @Column(name = "dinner_start")
    private Time dinnerStart;

    @Column(name = "dinner_end")
    private Time dinnerEnd;

    @Column(name = "is_dinner_closed")
    private Boolean isDinnerClosed;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Timestamp.from(Instant.now());
        updatedAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Timestamp.from(Instant.now());
    }

}
