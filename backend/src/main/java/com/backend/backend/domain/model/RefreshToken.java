package com.backend.backend.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "refresh_token")
@Data
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private UUID id;

    @OneToOne
    @Column(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token", unique = true, nullable = false)
    private String token;

    @Column(name = "expiryDate")
    private Timestamp expiryDate;

}
