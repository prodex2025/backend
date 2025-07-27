package com.backend.backend.domain.service;

import com.backend.backend.domain.dto.AllergyDto;
import com.backend.backend.domain.dto.DishesListDto;
import com.backend.backend.domain.dto.RequestDishDto;
import com.backend.backend.domain.model.Allergy;
import com.backend.backend.domain.model.Dish;
import com.backend.backend.domain.model.DishAllergy;
import com.backend.backend.domain.model.Restaurant;
import com.backend.backend.domain.repository.*;
import com.backend.backend.domain.service.mapper.DishMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class OwnerDishService {

    private final DishRepository dishRepository;
    private final RestaurantRepository restaurantRepository;
    private final AllergyRepository allergyRepository;
    private final DishAllergyRepository dishAllergyRepository;

    public OwnerDishService(DishRepository dishRepository,
                            RestaurantRepository restaurantRepository,
                            AllergyRepository allergyRepository,
                            DishAllergyRepository dishAllergyRepository) {
        this.dishRepository = dishRepository;
        this.restaurantRepository = restaurantRepository;
        this.allergyRepository = allergyRepository;
        this.dishAllergyRepository = dishAllergyRepository;
    }

    private static final int DEFAULT_PAGE_SIZE = 10;

    //店舗のメニュー取得
    public Page<DishesListDto> getDishes(UserDetails userDetails, UUID restaurantId, int page) {
        // loginIdを取得
        String loginId = userDetails.getUsername();

        // 店舗取得（存在チェック）
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "店舗が存在しません"));

        // 認証チェック
        validateOwner(loginId, restaurant);

        // ページングしてDishを取得
        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Dish> dishes = dishRepository.findByRestaurantId(pageable, restaurantId);

        return DishMapper.toDishesListDtoPage(dishes);
    }

    // 店舗メニュー登録
    @Transactional
    public void addDish(UserDetails userDetails, UUID restaurantId, RequestDishDto dto) {
        // dtoチェック
        if (dto == null) {
            throw new IllegalArgumentException("料理情報が指定されていません");
        }
        // loginIdを取得
        String loginId = userDetails.getUsername();

        // 店舗取得（存在チェック）
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "店舗が存在しません"));

        // 認証チェック
        validateOwner(loginId, restaurant);

        // 料理を登録
        Dish dish = DishMapper.toDishEntity(dto, restaurant);
        dishRepository.save(dish);

        // アレルギー情報の処理
        List<DishAllergy> dishAllergyList = createDishAllergies(dish, dto.getAllergyDtoList());

        // DishAllergyをまとめて登録
        dishAllergyRepository.saveAll(dishAllergyList);
    }

    // 店舗メニュー編集
    @Transactional
    public void editDish(UserDetails userDetails, UUID restaurantId, UUID dishId, RequestDishDto dto) {
        // dtoチェック
        if (dto == null) {
            throw new IllegalArgumentException("料理情報が指定されていません");
        }
        // loginIdを取得
        String loginId = userDetails.getUsername();

        // 店舗取得（存在チェック）
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "店舗が存在しません"));

        // 認証チェック
        validateOwner(loginId, restaurant);

        // 編集する料理を取得
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "料理が存在しません"));

        // 料理情報更新
        updateDishInfo(dish, dto);

        // アレルギー情報の更新
        updateDishAllergies(dish, dto.getAllergyDtoList());
    }

    // メニュー削除
    public void deleteDish(UserDetails userDetails, UUID restaurantId, UUID dishId) {
        // loginIdを取得
        String loginId = userDetails.getUsername();

        // 店舗取得（存在チェック）
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "店舗が存在しません"));

        // 認証チェック
        validateOwner(loginId, restaurant);

        // 料理削除
        dishRepository.deleteById(dishId);
    }

    // 認証チェック
    private void validateOwner(String loginId, Restaurant restaurant) {
        // nullチェックを追加
        if (loginId == null) {
            throw new AccessDeniedException("ログインユーザー情報が取得できません");
        }
        // 店舗ユーザー情報が不正
        if (restaurant.getUser() == null || restaurant.getUser().getLoginId() == null) {
            throw new IllegalStateException("店舗のユーザー情報が不正です");
        }
        // オーナー認証（loginIdで比較）
        if (!loginId.equals(restaurant.getUser().getLoginId())) {
            throw new AccessDeniedException("この店舗にアクセスする権限がありません");
        }
    }

    // アレルギー情報の処理
    private List<DishAllergy> createDishAllergies(Dish dish, List<AllergyDto> allergyDtoList) {
        if (allergyDtoList == null || allergyDtoList.isEmpty()) {
            return Collections.emptyList();
        }

        // アレルギーIDリストを取得
        List<UUID> allergyIds = allergyDtoList.stream()
                .map(AllergyDto::getId)
                .toList();

        // 一括取得
        List<Allergy> allergies = allergyRepository.findAllById(allergyIds);

        // 存在チェック
        if (allergies.size() != allergyIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "存在しないアレルギー情報が含まれています");
        }

        // DishAllergyエンティティに変換
        return allergies.stream()
                .map(allergy -> DishMapper.toDishAllergy(dish, allergy))
                .toList();
    }

    // 料理の基本情報を更新
    private void updateDishInfo(Dish dish, RequestDishDto dto) {
        try {
            Dish editedDish = DishMapper.setEditValues(dish, dto);
            dishRepository.save(editedDish);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "料理情報の更新に失敗しました", e);
        }
    }

    // 料理・アレルギー情報を更新
    private void updateDishAllergies(Dish dish, List<AllergyDto> allergyDtoList) {
        try {
            // 新しいアレルギー情報を作成
            List<DishAllergy> newDishAllergyList = createDishAllergies(dish, allergyDtoList);

            // 既存のアレルギー情報を削除
            dishAllergyRepository.deleteByDish(dish);

            // 新しいアレルギー情報を登録
            if (!newDishAllergyList.isEmpty()) {
                dishAllergyRepository.saveAll(newDishAllergyList);
            }

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "アレルギー情報の更新に失敗しました", e);
        }
    }

}
