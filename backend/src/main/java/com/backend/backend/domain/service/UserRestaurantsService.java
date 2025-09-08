package com.backend.backend.domain.service;

import com.backend.backend.domain.dto.RestaurantCategoryDetailDto;
import com.backend.backend.domain.dto.RestaurantDetailDto;
import com.backend.backend.domain.model.Restaurant;
import com.backend.backend.domain.model.RestaurantCategory;
import com.backend.backend.domain.model.StoreSchedule;
import com.backend.backend.domain.repository.RestaurantCategoryRepository;
import com.backend.backend.domain.repository.RestaurantRepository;
import com.backend.backend.domain.repository.StoreScheduleRepository;
import com.backend.backend.domain.service.mapper.RestaurantMapper;
import com.backend.backend.domain.service.s3.S3UrlService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class UserRestaurantsService {
    private final RestaurantCategoryRepository restaurantCategoryRepository;
    private final RestaurantRepository restaurantRepository;
    private final StoreScheduleRepository storeScheduleRepository;
    private final S3UrlService s3UrlService;

    public UserRestaurantsService(RestaurantCategoryRepository restaurantCategoryRepository,
                                  RestaurantRepository restaurantRepository,
                                  StoreScheduleRepository storeScheduleRepository,
                                  S3UrlService s3UrlService) {
        this.restaurantCategoryRepository = restaurantCategoryRepository;
        this.restaurantRepository = restaurantRepository;
        this.storeScheduleRepository = storeScheduleRepository;
        this.s3UrlService = s3UrlService;
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

    //店舗情報取得
    public RestaurantCategoryDetailDto getRestaurantDetail(UUID restaurantId){
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(()->new RuntimeException("店舗を取得できませんでした"));

        //中間テーブルの取得
        List<RestaurantCategory> restaurantCategoryList = restaurantCategoryRepository.findByRestaurantId(restaurantId);

        //DTOに変換したデータを取得して、値を返す
        return RestaurantMapper.toRestaurantDetailHeader(restaurant, restaurantCategoryList);
    }

    //店舗詳細情報取得
    public RestaurantDetailDto getRestaurantProfile(UUID restaurantId){
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(()->new jakarta.persistence.EntityNotFoundException("店舗を取得できませんでした"));

        //中間テーブルの取得
        List<StoreSchedule> storeSchedules = storeScheduleRepository.findByRestaurant(restaurant);

        //DTOに変換したデータを取得して、値を返す
        return RestaurantMapper.toRestaurantProfileDto(restaurant, storeSchedules);
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
            String signedUrl;
            String key = restaurant.getImageUrl();
            if (key == null || key.isBlank()) {
                signedUrl = "NO_IMAGE_URL";
            } else {
                try {
                    signedUrl = s3UrlService.generatePresignedUrl(key);
                } catch (Exception e) {
                    signedUrl = "NO_IMAGE_URL";
                }
            }
            List<RestaurantCategory> categories = categoryMap.getOrDefault(restaurant.getId(), List.of());
            return RestaurantMapper.toRestaurantCategoryDetailDto(categories, restaurant, signedUrl);
        });
    }

}

