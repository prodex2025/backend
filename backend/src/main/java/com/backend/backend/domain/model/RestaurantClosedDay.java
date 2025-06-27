package com.backend.backend.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "restaurants_closed_days")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantClosedDay {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "day_of_week", nullable = false)
    private Short dayOfWeek;

}
