package com.backend.backend.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "restaurant_interior_images")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantInteriorImage {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

}
