package com.backend.backend.app.controller;

import com.backend.backend.domain.model.Restaurant;
import com.backend.backend.domain.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @GetMapping("/{roomId}")
    public ResponseEntity<?> test(@PathVariable UUID roomId) {
        Optional<Restaurant> restaurants = restaurantRepository.findById(roomId);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/true")
    public ResponseEntity<?> test1() {
        List<Restaurant> restaurants = restaurantRepository.findByApprovedTrue();
        return ResponseEntity.ok(restaurants);
    }
    @GetMapping("/false")
    public ResponseEntity<?> test2() {
        List<Restaurant> restaurants = restaurantRepository.findByApprovedFalse();
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/true1")
    public ResponseEntity<?> test3() {
        List<Restaurant> restaurants = restaurantRepository.findByIsPublishedTrue();
        return ResponseEntity.ok(restaurants);
    }
    @GetMapping("/false1")
    public ResponseEntity<?> test4() {
        List<Restaurant> restaurants = restaurantRepository.findByIsPublishedFalse();
        return ResponseEntity.ok(restaurants);
    }
}
