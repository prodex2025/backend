package com.backend.backend.domain.service;

import com.backend.backend.domain.dto.OwnerRestaurantsDto;
import com.backend.backend.domain.dto.RequestAddRestaurantDto;
import com.backend.backend.domain.dto.RestaurantCategoryDto;
import com.backend.backend.domain.model.*;
import com.backend.backend.domain.repository.*;
import com.backend.backend.domain.service.mapper.RestaurantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OwnerRestaurantService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantCategoryRepository restaurantCategoryRepository;

    @Autowired
    private RestaurantClosedDayRepository restaurantClosedDayRepository;

    @Autowired
    private RestaurantBusinessHoursRepository restaurantBusinessHoursRepository;

    //店舗一覧を取得
    public Page<OwnerRestaurantsDto> getMyRestaurants(String loginId, int page) {
        //Userオブジェクトを取得
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが存在しません: " + loginId));

        //店舗一覧を分割取得
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Restaurant> restaurants = restaurantRepository.findByUserId(pageable, user.getId());

        // 店舗情報からDTOに変換して返却
        return restaurants.map(restaurant -> {
            // 中間テーブルからカテゴリ一覧を取得
            List<RestaurantCategory> categories = restaurantCategoryRepository.findByRestaurantId(restaurant.getId());

            // カテゴリEntityをDTOに変換
            List<RestaurantCategoryDto> categoryList = categories.stream()
                    .map(RestaurantCategoryDto::fromEntity)
                    .toList();

            // 店舗情報とカテゴリをDTOにまとめる
            return new OwnerRestaurantsDto(
                    restaurant.getId(),
                    restaurant.getName(),
                    restaurant.getAddress(),
                    restaurant.getPostCode(),
                    restaurant.getImageUrl(),
                    categoryList
            );
        });
    }

    //店舗新規登録
    @Transactional
    public void addMyRestaurant(RequestAddRestaurantDto restaurantDto, String loginId) {
        //Userオブジェクトを取得
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが存在しません: " + loginId));

        //店舗登録
        Restaurant restaurant = RestaurantMapper.toRestaurant(restaurantDto, user);
        Restaurant addRestaurant = restaurantRepository.save(restaurant);

        //定休日登録
        List<RestaurantClosedDay> closedDays = RestaurantMapper.toClosedDays(restaurantDto, addRestaurant);
        restaurantClosedDayRepository.saveAll(closedDays);

        //営業時間登録
        List<RestaurantBusinessHours> businessHours = RestaurantMapper.toBusinessHours(restaurantDto, addRestaurant);
        restaurantBusinessHoursRepository.saveAll(businessHours);

    }

}
