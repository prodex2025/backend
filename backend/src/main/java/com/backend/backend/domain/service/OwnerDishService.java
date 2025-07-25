package com.backend.backend.domain.service;

import com.backend.backend.domain.dto.DishesListDto;
import com.backend.backend.domain.model.Dish;
import com.backend.backend.domain.model.Restaurant;
import com.backend.backend.domain.repository.DishRepository;
import com.backend.backend.domain.repository.RestaurantCategoryRepository;
import com.backend.backend.domain.repository.RestaurantRepository;
import com.backend.backend.domain.service.mapper.DishMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class OwnerDishService {

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    //店舗のメニュー取得
    public Page<DishesListDto> getDishes(UserDetails userDetails, UUID restaurantId, int page) {
        String loginId = userDetails.getUsername();

        // nullチェックを追加
        if (loginId == null || loginId.isEmpty()) {
            throw new AccessDeniedException("ログインユーザー情報が取得できません");
        }

        // 店舗取得（存在チェック）
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "店舗が存在しません。"));

        // オーナー認証（loginIdで比較）
        if (!loginId.equals(restaurant.getUser().getLoginId())) {
            throw new AccessDeniedException("この店舗にアクセスする権限がありません");
        }

        // ページングしてDishを取得
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Dish> dishes = dishRepository.findByRestaurantId(pageable, restaurantId);

        return DishMapper.toDishesListDtoPage(dishes);
    }

}
