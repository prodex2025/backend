package com.backend.backend.domain.repository;

import com.backend.backend.domain.model.Restaurant;
import com.backend.backend.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

}
