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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OwnerDishService {

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private AllergyRepository allergyRepository;

    @Autowired
    private DishAllergyRepository dishAllergyRepository;

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
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
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

}
