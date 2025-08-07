package com.backend.backend.app.controller;

import com.backend.backend.domain.dto.RestaurantCategoryDetailDto;
import com.backend.backend.domain.service.UserRestaurantsService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {
    private final UserRestaurantsService userRestaurantsService;

    public RestaurantController(UserRestaurantsService userRestaurantsService) {
        this.userRestaurantsService = userRestaurantsService;
    }

    @GetMapping
    public ResponseEntity<?> getAllRestaurants(@RequestParam(defaultValue = "0") int page, @RequestParam(required = false) String keyword, @RequestParam(required = false) List<UUID> categoryIds) {
        Page<RestaurantCategoryDetailDto> restaurantList = userRestaurantsService.getAllRestaurants(page, keyword, categoryIds);
        return ResponseEntity.ok(restaurantList);
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantCategoryDetailDto> getRestaurantById(@PathVariable UUID restaurantId) {
        RestaurantCategoryDetailDto restaurantDetail = userRestaurantsService.getRestaurantDetail(restaurantId);
        return ResponseEntity.ok(restaurantDetail);
    }


}
