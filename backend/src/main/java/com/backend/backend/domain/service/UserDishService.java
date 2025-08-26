package com.backend.backend.domain.service;

import com.backend.backend.domain.dto.Dish3dDto;
import com.backend.backend.domain.dto.DishesListDto;
import com.backend.backend.domain.model.Dish;
import com.backend.backend.domain.model.Restaurant;
import com.backend.backend.domain.repository.DishRepository;
import com.backend.backend.domain.repository.RestaurantRepository;
import com.backend.backend.domain.service.mapper.DishMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class UserDishService {
    private final DishRepository dishRepository;
    private final RestaurantRepository restaurantRepository;

    public UserDishService(DishRepository dishRepository,
                           RestaurantRepository restaurantRepository) {
        this.dishRepository = dishRepository;
        this.restaurantRepository = restaurantRepository;
    }

    private static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 指定された店舗IDのメニュー一覧をページネーション形式で取得します。
     *
     * @param restaurantId 店舗のUUID
     * @param page 取得するページ番号
     * @return メニュー情報のページ
     */
    public Page<DishesListDto> getDishes(UUID restaurantId, int page) {

        // 店舗取得（存在チェック）
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "店舗が存在しません"));

        // ページングしてDishを取得
        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Dish> dishes = dishRepository.findByRestaurantId(pageable, restaurantId);

        return DishMapper.toDishesListDtoPage(dishes);
    }

    public Dish3dDto getDetails(UUID restaurantId, UUID menuId){

        // 店舗取得（存在チェック）
        Dish dish = dishRepository.findByRestaurantIdAndId(restaurantId, menuId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "料理が存在しません"));

        return DishMapper.toDish3dDto(dish);


    }
}
