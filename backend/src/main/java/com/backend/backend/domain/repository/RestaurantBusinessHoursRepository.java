package com.backend.backend.domain.repository;

import com.backend.backend.domain.model.RestaurantBusinessHours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RestaurantBusinessHoursRepository extends JpaRepository<RestaurantBusinessHours, UUID> {
    //restaurantIdから店舗の営業時間一覧を取得
    List<RestaurantBusinessHours> findByRestaurantId(UUID restaurantId);
}
