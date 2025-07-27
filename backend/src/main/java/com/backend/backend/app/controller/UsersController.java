package com.backend.backend.app.controller;

import com.backend.backend.domain.dto.RestaurantCategoryDetailDto;
import com.backend.backend.domain.service.UserRestaurantsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/restaurants")
public class UsersController {
    @Autowired
    private UserRestaurantsService userRestaurantsService;

    @GetMapping
    public ResponseEntity<?> getAllRestaurants(@RequestParam(defaultValue = "0") int p, @RequestParam(required = false) String keyword, @RequestParam(required = false) List<UUID> categoryIds) {
        Page<RestaurantCategoryDetailDto> restaurantList = userRestaurantsService.getAllRestaurants(p, keyword, categoryIds);
        return ResponseEntity.ok(restaurantList);
    }




}
