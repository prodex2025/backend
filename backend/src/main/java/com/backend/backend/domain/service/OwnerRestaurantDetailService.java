package com.backend.backend.domain.service;

import com.backend.backend.domain.dto.RequestEditRestaurantHeaderDto;
import com.backend.backend.domain.dto.RestaurantBasicUpdateDto;
import com.backend.backend.domain.dto.RestaurantCategoryDetailDto;
import com.backend.backend.domain.dto.RestaurantDetailDto;
import com.backend.backend.domain.model.Category;
import com.backend.backend.domain.model.Restaurant;
import com.backend.backend.domain.model.RestaurantCategory;
import com.backend.backend.domain.model.StoreSchedule;
import com.backend.backend.domain.repository.CategoryRepository;
import com.backend.backend.domain.repository.RestaurantCategoryRepository;
import com.backend.backend.domain.repository.RestaurantRepository;
import com.backend.backend.domain.repository.StoreScheduleRepository;
import com.backend.backend.domain.service.mapper.RestaurantMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Service
public class OwnerRestaurantDetailService {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantCategoryRepository restaurantCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final StoreScheduleRepository storeScheduleRepository;

    public OwnerRestaurantDetailService(RestaurantRepository restaurantRepository,
                                        RestaurantCategoryRepository restaurantCategoryRepository,
                                        CategoryRepository categoryRepository,
                                        StoreScheduleRepository storeScheduleRepository) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantCategoryRepository = restaurantCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.storeScheduleRepository = storeScheduleRepository;
    }

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

    //店舗詳細のヘッダー部分を編集
    @Transactional
    public void editRestaurantHeader(UserDetails userDetails, UUID restaurantId, RequestEditRestaurantHeaderDto requestDto) {
        //loginId取得
        String loginId = userDetails.getUsername();

        //店舗Idから店舗取得
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(()->new RuntimeException("店舗を取得できませんでした"));

        //オーナー以外がアクセスした場合
        if (loginId == null || !loginId.equals(restaurant.getUser().getLoginId())) {
            throw new AccessDeniedException("この店舗にアクセスする権限がありません");
        }
        //値を上書き
        Restaurant editRestaurant = RestaurantMapper.toEditRestaurantDetailHeader(restaurant, requestDto);
        restaurantRepository.save(editRestaurant);

        //中間テーブルの削除
        restaurantCategoryRepository.deleteByRestaurant(restaurant);

        //カテゴリを再登録
        List<RestaurantCategory> restaurantCategoryList = Optional.ofNullable(requestDto.getCategoryDtoList())
                .orElse(Collections.emptyList()).stream()
                .map(restaurantCategory -> {
                    Category category = categoryRepository.findById(restaurantCategory.getId())
                            .orElseThrow(() -> new RuntimeException("値を取得できませんでした"));

                    return RestaurantMapper.toEditRestaurantCategoryHeader(restaurant, category);
                }).toList();
        //RestaurantCategoryをまとめて登録
        restaurantCategoryRepository.saveAll(restaurantCategoryList);
    }

    //店舗削除
    @Transactional
    public void deleteRestaurant(UserDetails userDetails, UUID restaurantId) {
        //loginId取得
        String loginId = userDetails.getUsername();

        //店舗Idから店舗取得
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(()->new RuntimeException("店舗を取得できませんでした"));

        //オーナー以外がアクセスした場合
        if (loginId == null || !loginId.equals(restaurant.getUser().getLoginId())) {
            throw new AccessDeniedException("この店舗にアクセスする権限がありません");
        }
        //店舗削除・中間テーブルの削除
        restaurantRepository.delete(restaurant);
        restaurantCategoryRepository.deleteByRestaurant(restaurant);
    }

    // 店舗詳細情報を表示
    public RestaurantDetailDto getRestaurantDetail(UserDetails userDetails, UUID restaurantId) {
        // loginId取得
        String loginId = userDetails.getUsername();

        // 店舗Idから店舗取得
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(()->new RuntimeException("店舗を取得できませんでした"));

        // オーナー以外がアクセスした場合
        if (loginId == null || !loginId.equals(restaurant.getUser().getLoginId())) {
            throw new AccessDeniedException("この店舗にアクセスする権限がありません");
        }
        // 定休日・営業時間を取得
        List<StoreSchedule> storeSchedules = storeScheduleRepository.findByRestaurant(restaurant);

        // DTOに変換したデータを取得して、値を返す
        return RestaurantMapper.toRestaurantDetailDto(restaurant, storeSchedules);
    }

    // 店舗基本情報を編集
    @Transactional
    public void editRestaurantDetailBasic(UserDetails userDetails, UUID restaurantId, RestaurantBasicUpdateDto dto) {
        // loginId取得
        String loginId = userDetails.getUsername();

        // 店舗Idから店舗取得
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(()->new RuntimeException("店舗を取得できませんでした"));

        // オーナー以外がアクセスした場合
        if (loginId == null || !loginId.equals(restaurant.getUser().getLoginId())) {
            throw new AccessDeniedException("この店舗にアクセスする権限がありません");
        }

        // 文字列は空文字もスキップ
        applyIfHasText(dto.getAddress(), restaurant::setAddress);
        applyIfHasText(dto.getPhone(), restaurant::setPhone);
        applyIfHasText(dto.getEmail(), restaurant::setEmail);
        applyIfHasText(dto.getInteriorImageUrl(), restaurant::setInteriorImageUrl);

        restaurantRepository.save(restaurant);
    }

    // 空文字・null・空白出ない場合値をセット
    private void applyIfHasText(String value, Consumer<String> setter) {
        if (StringUtils.hasText(value)) {
            setter.accept(value);
        }
    }

}
