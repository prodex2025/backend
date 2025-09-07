package com.backend.backend.domain.service;

import com.backend.backend.domain.dto.OwnerRestaurantsDto;
import com.backend.backend.domain.dto.RequestAddRestaurantDto;
import com.backend.backend.domain.dto.RestaurantCategoryDto;
import com.backend.backend.domain.model.*;
import com.backend.backend.domain.repository.*;
import com.backend.backend.domain.service.mapper.RestaurantMapper;
import com.backend.backend.domain.service.s3.S3Mover;
import com.backend.backend.domain.service.s3.S3UrlService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OwnerRestaurantService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantCategoryRepository restaurantCategoryRepository;
    private final StoreScheduleRepository storeScheduleRepository;
    private final S3Mover s3Mover;
    private final S3UrlService s3UrlService;

    public OwnerRestaurantService(UserRepository userRepository,
                                  RestaurantRepository restaurantRepository,
                                  RestaurantCategoryRepository restaurantCategoryRepository,
                                  StoreScheduleRepository storeScheduleRepository,
                                  S3Mover s3Mover,
                                  S3UrlService s3UrlService) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.restaurantCategoryRepository = restaurantCategoryRepository;
        this.storeScheduleRepository = storeScheduleRepository;
        this.s3Mover = s3Mover;
        this.s3UrlService = s3UrlService;
    }

    // 店舗一覧を取得
    public Page<OwnerRestaurantsDto> getMyRestaurants(String loginId, int page) {
        // Userオブジェクトを取得
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが存在しません: " + loginId));

        // 店舗一覧を分割取得
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Restaurant> restaurants = restaurantRepository.findByUserId(pageable, user.getId());

        // 店舗情報からDTOに変換して返却
        return restaurants.map(restaurant -> {
            String signedUrl;
            String key = restaurant.getImageUrl();
            if (key == null || key.isBlank()) {
                signedUrl = "NO_IMAGE_URL";
            } else {
                try {
                    signedUrl = s3UrlService.generatePresignedUrl(key);
                } catch (Exception e) {
                    signedUrl = "NO_IMAGE_URL";
                }
            }

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
                    signedUrl,
                    categoryList
            );
        });
    }

    //店舗新規登録
    @Transactional
    public void addMyRestaurant(RequestAddRestaurantDto restaurantDto, String loginId) {
        // Userオブジェクトを取得
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが存在しません: " + loginId));

        // 店舗登録
        Restaurant restaurant = RestaurantMapper.toRestaurant(restaurantDto, user);
        restaurantRepository.save(restaurant);

        String exteriorKey = moveToFinal(restaurantDto.getExteriorTmpKey(), "exterior", restaurant.getId());
        String interiorKey = moveToFinal(restaurantDto.getInteriorTmpKey(), "interior", restaurant.getId());

        // 管理エンティティにセット
        restaurant.setImageUrl(exteriorKey);
        restaurant.setInteriorImageUrl(interiorKey);
        restaurantRepository.save(restaurant);

        // tmp削除
        s3Mover.deleteBatch(List.of(restaurantDto.getExteriorTmpKey(), restaurantDto.getInteriorTmpKey()));

        // 定休日・営業時間の登録
        List<StoreSchedule> storeSchedules = RestaurantMapper.toStoreSchedule(restaurantDto, restaurant);
        storeScheduleRepository.saveAll(storeSchedules);
    }

    // 仮保存から本番の保存にコピー
    private String moveToFinal(String tmpKey, String kind, UUID restaurantId) {
        System.out.println(tmpKey);
        // tmpKey
        String fileName = tmpKey.substring(tmpKey.lastIndexOf('/') + 1);
        String destKey  = "restaurants/%s/%s/%s".formatted(restaurantId, kind, fileName);

        s3Mover.copy(tmpKey, destKey);

        return destKey;
    }

}
