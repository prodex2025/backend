package com.backend.backend.domain.repository;

import com.backend.backend.domain.model.Restaurant;
import com.backend.backend.domain.model.StoreSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StoreScheduleRepository extends JpaRepository<StoreSchedule, UUID> {
    List<StoreSchedule> findByRestaurant(Restaurant restaurant);
}
