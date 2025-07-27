package com.backend.backend.domain.service;

import com.backend.backend.domain.dto.RestaurantCategoryDetailDto;
import com.backend.backend.domain.dto.RestaurantCategoryDto;
import com.backend.backend.domain.model.Restaurant;
import com.backend.backend.domain.model.RestaurantCategory;
import com.backend.backend.domain.repository.RestaurantCategoryRepository;
import com.backend.backend.domain.repository.RestaurantRepository;
import com.backend.backend.domain.service.mapper.RestaurantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class UserRestaurantsService {
    private final RestaurantCategoryRepository restaurantCategoryRepository;
    private final RestaurantRepository restaurantRepository;

    public UserRestaurantsService(RestaurantCategoryRepository restaurantCategoryRepository, RestaurantRepository restaurantRepository) {
        this.restaurantCategoryRepository = restaurantCategoryRepository;
        this.restaurantRepository = restaurantRepository;
    }

    private static final int DEFAULT_PAGE_SIZE = 10;

    // 店舗一覧取得
    public Page<RestaurantCategoryDetailDto> getAllRestaurants(int page, String keyword, List<UUID> categoryIds) {
        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE);
        Page<Restaurant> restaurants;

        if (keyword != null && !keyword.isBlank() && categoryIds != null && !categoryIds.isEmpty()) {
            String likeKeyword = "%" + keyword + "%";
            restaurants = restaurantCategoryRepository.findRestaurantsByCategoryIdsAndKeywordAndIsPublishedTure(categoryIds, likeKeyword, pageable);
        } else if (categoryIds != null && !categoryIds.isEmpty()) {
            restaurants = restaurantCategoryRepository.findRestaurantsByCategoryIdsAndIsPublishedTrue(categoryIds, pageable);
        } else if (keyword != null && !keyword.isBlank()) {
            restaurants = restaurantRepository.findByIsPublishedTrueAndNameContaining(pageable, keyword);
        } else {
            restaurants = restaurantRepository.findByIsPublishedTrue(pageable);
        }

        return toDtoPage(restaurants);
    }

    private Page<RestaurantCategoryDetailDto> toDtoPage(Page<Restaurant> restaurants) {
        List<UUID> restaurantIds = restaurants
                .stream()
                .map(Restaurant::getId)
                .toList();

        List<RestaurantCategory> allCategories = restaurantCategoryRepository.findByRestaurantIdIn(restaurantIds);

        // Map<restaurantId, List<RestaurantCategory>> に変換
        Map<UUID, List<RestaurantCategory>> categoryMap = allCategories.stream()
                .collect(Collectors.groupingBy(rc -> rc.getRestaurant().getId()));

        return restaurants.map(restaurant -> {
            List<RestaurantCategory> categories = categoryMap.getOrDefault(restaurant.getId(), List.of());
            return RestaurantMapper.toRestaurantCategoryDetailDto(categories, restaurant);
        });
    }

}

