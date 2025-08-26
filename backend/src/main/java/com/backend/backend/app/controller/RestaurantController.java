package com.backend.backend.app.controller;

import com.backend.backend.domain.dto.Dish3dDto;
import com.backend.backend.domain.dto.DishesListDto;
import com.backend.backend.domain.dto.RestaurantCategoryDetailDto;
import com.backend.backend.domain.model.Dish;
import com.backend.backend.domain.service.UserDishService;
import com.backend.backend.domain.service.UserRestaurantsService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {
    private final UserRestaurantsService userRestaurantsService;
    private final UserDishService userDishService;

    public RestaurantController(UserRestaurantsService userRestaurantsService, UserDishService userDishService) {
        this.userRestaurantsService = userRestaurantsService;
        this.userDishService = userDishService;
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

    @GetMapping("/{restaurantId}/menus")
    public ResponseEntity<Page<DishesListDto>> getDishes(@PathVariable UUID restaurantId,
                                                         @RequestParam(name = "page", defaultValue = "0") int page) {
        Page<DishesListDto> dishes = userDishService.getDishes(restaurantId, page);
        return ResponseEntity.ok(dishes);
    }

    @GetMapping("/{restaurantId}/dishes/{dishId}")
    public ResponseEntity<Dish3dDto> getDishDetail(@PathVariable UUID restaurantId,
                                                   @PathVariable UUID dishId) {
        Dish3dDto dishDetail = userDishService.getDetails(restaurantId, dishId);
        return ResponseEntity.ok(dishDetail);
    }

}
