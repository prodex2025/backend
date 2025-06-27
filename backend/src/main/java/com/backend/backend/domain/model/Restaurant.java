package com.backend.backend.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "restaurants")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Restaurant {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "post_code")
    private String postCode;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "description")
    private String description;

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
