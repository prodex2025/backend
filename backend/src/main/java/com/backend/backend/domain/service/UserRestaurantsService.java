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
import java.util.UUID;


@Service
public class UserRestaurantsService {
    @Autowired
    private RestaurantCategoryRepository restaurantCategoryRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    // 店舗一覧取得
    public Page<RestaurantCategoryDetailDto> getAllRestaurants(int p, String keyword, List<UUID> categoryIds) {
        Pageable pageable = PageRequest.of(p, 10);
        Page<Restaurant> restaurants;

        if (keyword != null && !keyword.isBlank() && categoryIds != null && !categoryIds.isEmpty()) {
            String likeKeyword = "%" + keyword + "%";
            restaurants = restaurantCategoryRepository.findRestaurantsByCategoryIdsAndKeywordAndIsPublishedTure(categoryIds, likeKeyword, pageable);
        } else if (categoryIds != null && !categoryIds.isEmpty()) {
            restaurants = restaurantCategoryRepository.findRestaurantsByCategoryIdsAndIsPublishedTure(categoryIds, pageable);
        } else if (keyword != null && !keyword.isBlank()) {
            pageable = PageRequest.of(p, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
            restaurants = restaurantRepository.findByIsPublishedTrueAndNameContaining(pageable, keyword);
        } else {
            pageable = PageRequest.of(p, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
            restaurants = restaurantRepository.findByIsPublishedTrue(pageable);
        }

        return toDtoPage(restaurants);
    }

    private Page<RestaurantCategoryDetailDto> toDtoPage(Page<Restaurant> restaurants) {
        return restaurants.map(restaurant -> {
            List<RestaurantCategory> categories = restaurantCategoryRepository.findByRestaurantId(restaurant.getId());
            return RestaurantMapper.toGetRestaurantList(categories, restaurant);
        });
    }
}

