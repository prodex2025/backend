package com.backend.backend.app.controller;

import com.backend.backend.domain.model.Restaurant;
import com.backend.backend.domain.repository.RestaurantCategoryRepository;
import com.backend.backend.domain.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    //店舗のリポジトリを扱う
    @Autowired
    private RestaurantRepository restaurantRepository;

    //店舗とカテゴリーの中間リポジトリを扱う
    @Autowired
    private RestaurantCategoryRepository restaurantCategoryRepository;
    
    //1 メソッド書く
    @GetMapping("/true")
    public ResponseEntity<?> findByApprovedTrue(@RequestParam(defaultValue = "0") int p) {
        Page<Restaurant> restaurants = restaurantRepository.findByApprovedTrue(PageRequest.of(p, 10));
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/approvedFalse")
    public ResponseEntity<?> findByApprovedFalse(@RequestParam(defaultValue = "0") int p) {
        Page<Restaurant> restaurants = restaurantRepository.findByApprovedFalse(PageRequest.of(p, 10));
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/publishedTrue")
    public ResponseEntity<?> findByIsPublishedTrue(@RequestParam(defaultValue = "0") int p) {
        Page<Restaurant> restaurants = restaurantRepository.findByIsPublishedTrue(PageRequest.of(p, 10));
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/publishedFalse")
    public ResponseEntity<?> findByIsPublishedFalse(@RequestParam(defaultValue = "0") int p) {
        Page<Restaurant> restaurants = restaurantRepository.findByIsPublishedFalse(PageRequest.of(p, 10));
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/approvedTrueAndName")
    public ResponseEntity<?> findByApprovedTrueAndNameContaining(@RequestParam(defaultValue = "0") int p, @RequestParam String keyword) {
        Page<Restaurant> restaurants = restaurantRepository.findByApprovedTrueAndNameContaining(PageRequest.of(p, 10), keyword);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/approvedFalseAndName")
    public ResponseEntity<?> findByApprovedFalseAndNameContaining(@RequestParam(defaultValue = "0") int p, @RequestParam String keyword) {
        Page<Restaurant> restaurants = restaurantRepository.findByApprovedFalseAndNameContaining(PageRequest.of(p, 10), keyword);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/publishedTrueAndName")
    public ResponseEntity<?> findByIsPublishedTrueAndNameContaining(@RequestParam(defaultValue = "0") int p, @RequestParam String keyword) {
        Page<Restaurant> restaurants = restaurantRepository.findByIsPublishedTrueAndNameContaining(PageRequest.of(p, 10), keyword);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/publishedFalseAndName")
    public ResponseEntity<?> findByIsPublishedFalseAndNameContaining(@RequestParam(defaultValue = "0") int p, @RequestParam String keyword) {
        Page<Restaurant> restaurants = restaurantRepository.findByIsPublishedFalseAndNameContaining(PageRequest.of(p, 10), keyword);
        return ResponseEntity.ok(restaurants);
    }
}
