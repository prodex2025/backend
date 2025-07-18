package com.backend.backend.domain.service;

import com.backend.backend.domain.dto.RestaurantCategoryDetailDto;
import com.backend.backend.domain.model.Restaurant;
import com.backend.backend.domain.model.RestaurantCategory;
import com.backend.backend.domain.repository.RestaurantCategoryRepository;
import com.backend.backend.domain.repository.RestaurantRepository;
import com.backend.backend.domain.service.mapper.RestaurantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OwnerRestaurantDetailService {
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantCategoryRepository restaurantCategoryRepository;

    //店舗詳細のヘッダー部分を取得
    public RestaurantCategoryDetailDto getMyRestaurantHeader(UserDetails userDetails, UUID restaurantId) {
        //loginId取得
        String loginId = userDetails.getUsername();

        //店舗Idから店舗取得
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(()->new RuntimeException("店舗を取得できませんでした"));

        //オーナー以外がアクセスした場合
        if (loginId == null || !loginId.equals(restaurant.getUser().getLoginId())) {
            throw new AccessDeniedException("この店舗にアクセスする権限がありません");
        }

        //中間テーブルの取得
        List<RestaurantCategory> restaurantCategoryList = restaurantCategoryRepository.findByRestaurantId(restaurantId);

        //DTOに変換したデータを取得して、値を返す
        return RestaurantMapper.toRestaurantDetailHeader(restaurant, restaurantCategoryList);
    }

}
