package com.backend.backend.domain.repository;

import com.backend.backend.domain.model.RestaurantClosedDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RestaurantClosedDayRepository extends JpaRepository<RestaurantClosedDay, UUID> {
    //restaurantIdから店舗の定休日一覧を取得
    List<RestaurantClosedDay> findByRestaurantId(UUID restaurantId);
}
