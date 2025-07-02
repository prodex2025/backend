package com.backend.backend.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.UUID;

@Entity
@Table(name = "restaurants_business_hours")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantBusinessHours {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "day_of_week", nullable = false)
    private Short dayOfWeek;

    @Column(name = "open_time", nullable = false)
    private Time openTime;

    @Column(name = "close_time", nullable = false)
    private Time closeTime;
    
}
