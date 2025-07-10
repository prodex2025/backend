package com.backend.backend.domain.repository;

import com.backend.backend.domain.model.Dish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DishRepository extends JpaRepository<Dish, UUID> {
    //店舗Idに該当する料理を表示
    Page<Dish> findByRestaurantId(Pageable pageable, UUID restaurantId);
}
