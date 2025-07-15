package com.backend.backend.domain.service;

import com.backend.backend.domain.dto.OwnerRestaurantsDto;
import com.backend.backend.domain.dto.RestaurantCategoryDto;
import com.backend.backend.domain.model.Restaurant;
import com.backend.backend.domain.model.RestaurantCategory;
import com.backend.backend.domain.model.User;
import com.backend.backend.domain.repository.RestaurantCategoryRepository;
import com.backend.backend.domain.repository.RestaurantRepository;
import com.backend.backend.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OwnerRestaurantService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantCategoryRepository restaurantCategoryRepository;

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
}
