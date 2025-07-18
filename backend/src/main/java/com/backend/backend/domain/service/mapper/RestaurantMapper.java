package com.backend.backend.domain.service.mapper;

import com.backend.backend.domain.dto.RequestAddRestaurantDto;
import com.backend.backend.domain.model.Restaurant;
import com.backend.backend.domain.model.RestaurantBusinessHours;
import com.backend.backend.domain.model.RestaurantClosedDay;
import com.backend.backend.domain.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RestaurantMapper {
    //店舗登録
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

    //定休日
    public static List<RestaurantClosedDay> toClosedDays(RequestAddRestaurantDto dto, Restaurant restaurant) {
        return Optional.ofNullable(dto.getClosedDayDtoList())
                .orElse(Collections.emptyList()).stream()
                .map(closedDay -> {
                    RestaurantClosedDay rcd = new RestaurantClosedDay();
                    rcd.setRestaurant(restaurant);
                    rcd.setDayOfWeek(closedDay.getDayOfWeek());
                    return rcd;
                })
                .collect(Collectors.toList());
    }

    //営業時間
    public static List<RestaurantBusinessHours> toBusinessHours(RequestAddRestaurantDto dto, Restaurant restaurant) {
        return Optional.ofNullable(dto.getBusinessHoursDtoList())
                .orElse(Collections.emptyList()).stream()
                .map(hours -> {
                    RestaurantBusinessHours rbh = new RestaurantBusinessHours();
                    rbh.setRestaurant(restaurant);
                    rbh.setDayOfWeek(hours.getDayOfWeek());
                    rbh.setOpenTime(hours.getOpenTime());
                    rbh.setCloseTime(hours.getCloseTime());
                    return rbh;
                })
                .collect(Collectors.toList());
    }

}
