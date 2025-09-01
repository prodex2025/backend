package com.backend.backend.domain.service.mapper;

import com.backend.backend.domain.dto.*;
import com.backend.backend.domain.model.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RestaurantMapper {
    // 店舗登録
    public static Restaurant toRestaurant(RequestAddRestaurantDto dto, User user) {
        Restaurant restaurant = new Restaurant();
        restaurant.setUser(user);
        restaurant.setName(dto.getName());
        restaurant.setAddress(dto.getAddress());
        restaurant.setPostCode(dto.getPostCode());
        restaurant.setPhone(dto.getPhone());
        restaurant.setEmail("example@example.com");
        restaurant.setDescription(dto.getDescription());
        restaurant.setImageUrl(dto.getImageUrl());
        restaurant.setInteriorImageUrl(dto.getInteriorImageUrl());
        restaurant.setCertificate(dto.getCertificate());
        restaurant.setApproved(false);
        restaurant.setIsPublished(false);
        return restaurant;
    }

    // 定休日・営業時間
    public static List<StoreSchedule> toStoreSchedule(RequestAddRestaurantDto dto, Restaurant restaurant) {
        return Optional.ofNullable(dto.getStoreScheduleDtoList())
                .orElse(Collections.emptyList()).stream()
                .map(storeScheduleDto -> {
                    StoreSchedule storeSchedule = new StoreSchedule();
                    storeSchedule.setRestaurant(restaurant);
                    storeSchedule.setDayOfWeek(storeScheduleDto.getDayOfWeek());
                    storeSchedule.setIsClosed(storeScheduleDto.getIsClosed());
                    storeSchedule.setLunchStart(storeScheduleDto.getLunchStart());
                    storeSchedule.setLunchEnd(storeScheduleDto.getLunchEnd());
                    storeSchedule.setIsLunchClosed(storeScheduleDto.getIsLunchClosed());
                    storeSchedule.setDinnerStart(storeScheduleDto.getDinnerStart());
                    storeSchedule.setDinnerEnd(storeScheduleDto.getDinnerEnd());
                    storeSchedule.setIsDinnerClosed(storeScheduleDto.getIsDinnerClosed());
                    return storeSchedule;
                })
                .toList();
    }

    // 店舗詳細ヘッダーDTOセット
    public static RestaurantCategoryDetailDto toRestaurantDetailHeader(Restaurant restaurant, List<RestaurantCategory> restaurantCategory) {
       // DTOにセット
        RestaurantCategoryDetailDto restaurantCategoryDetailDto = new RestaurantCategoryDetailDto();
        restaurantCategoryDetailDto.setRestaurantId(restaurant.getId());
        restaurantCategoryDetailDto.setRestaurantName(restaurant.getName());
        restaurantCategoryDetailDto.setRestaurantAddress(restaurant.getAddress());
        restaurantCategoryDetailDto.setRestaurantPostCode(restaurant.getPostCode());
        restaurantCategoryDetailDto.setCategoryDtoList(restaurantCategory.stream().map(RestaurantCategoryDto::fromEntity).toList());

        return restaurantCategoryDetailDto;
    }

    // 店舗詳細ヘッダー編集
    public static Restaurant toEditRestaurantDetailHeader(Restaurant restaurant, RequestEditRestaurantHeaderDto editRestaurant) {
        // 値をセット
        restaurant.setName(editRestaurant.getRestaurantName());
        restaurant.setAddress(editRestaurant.getRestaurantAddress());
        restaurant.setPostCode(editRestaurant.getRestaurantPostCode());
        restaurant.setImageUrl(editRestaurant.getImageUrl());

        return restaurant;
    }

    // 店舗詳細ヘッダーカテゴリ編集
    public static RestaurantCategory toEditRestaurantCategoryHeader(Restaurant restaurant, Category category) {
        RestaurantCategory restaurantCategory = new RestaurantCategory();
        restaurantCategory.setCategory(category);
        restaurantCategory.setRestaurant(restaurant);

        return restaurantCategory;
    }

    // 店舗詳細情報DTOセット
    public static RestaurantDetailDto toRestaurantDetailDto(Restaurant restaurant, List<StoreSchedule> storeSchedules) {
        //DTOセット
        RestaurantDetailDto restaurantDetailDto = new RestaurantDetailDto();
        restaurantDetailDto.setId(restaurant.getId());
        restaurantDetailDto.setAddress(restaurant.getAddress());
        restaurantDetailDto.setPhone(restaurant.getPhone());
        restaurantDetailDto.setEmail(restaurant.getEmail());
        restaurantDetailDto.setDescription(restaurant.getDescription());
        restaurantDetailDto.setInteriorImageUrl(restaurant.getInteriorImageUrl());
        restaurantDetailDto.setStoreScheduleDtoList(storeSchedules.stream().map(StoreScheduleDto::fromEntity).toList());

        return restaurantDetailDto;
    }

    // 店舗一覧DTO返却
    public static RestaurantCategoryDetailDto toRestaurantCategoryDetailDto(List<RestaurantCategory> categories, Restaurant restaurant) {
        // カテゴリEntityをDTOに変換
        List<RestaurantCategoryDto> categoryList = categories.stream()
                .map(RestaurantCategoryDto::fromEntity)
                .toList();

        // 店舗情報とカテゴリをDTOにまとめる
        return new RestaurantCategoryDetailDto(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getPostCode(),
                categoryList
        );
    }

    //店舗情報
    public static RestaurantDetailDto toRestaurantProfileDto(Restaurant restaurant, List<StoreSchedule> storeSchedules){
        // カテゴリEntityをDTOに変換
        List<StoreScheduleDto> scheduleList = storeSchedules.stream()
                .map(StoreScheduleDto::fromEntity)
                .toList();

        // 店舗情報とカテゴリをDTOにまとめる
        return new RestaurantDetailDto(
            restaurant.getId(),
            restaurant.getAddress(),
            restaurant.getPhone(),
            restaurant.getEmail(),
            restaurant.getDescription(),
            restaurant.getInteriorImageUrl(),
            scheduleList
        );
    }

    // 定休日・営業時間編集
    public static StoreSchedule editSchedule(StoreSchedule storeSchedule, StoreScheduleDto storeScheduleDto ) {
        storeSchedule.setDayOfWeek(storeScheduleDto.getDayOfWeek());
        storeSchedule.setIsClosed(storeScheduleDto.getIsClosed());
        storeSchedule.setLunchStart(storeScheduleDto.getLunchStart());
        storeSchedule.setLunchEnd(storeScheduleDto.getLunchEnd());
        storeSchedule.setIsLunchClosed(storeScheduleDto.getIsLunchClosed());
        storeSchedule.setDinnerStart(storeScheduleDto.getDinnerStart());
        storeSchedule.setDinnerEnd(storeScheduleDto.getDinnerEnd());
        storeSchedule.setIsDinnerClosed(storeScheduleDto.getIsDinnerClosed());

        return storeSchedule;
    }

}
