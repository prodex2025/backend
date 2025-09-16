package com.backend.backend.domain.service;

import com.backend.backend.domain.dto.*;
import com.backend.backend.domain.model.*;
import com.backend.backend.domain.repository.CategoryRepository;
import com.backend.backend.domain.repository.RestaurantCategoryRepository;
import com.backend.backend.domain.repository.RestaurantRepository;
import com.backend.backend.domain.repository.StoreScheduleRepository;
import com.backend.backend.domain.service.mapper.RestaurantMapper;
import com.backend.backend.domain.service.s3.S3Mover;
import com.backend.backend.domain.service.s3.S3UrlService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Consumer;

import static org.springframework.util.StringUtils.hasText;

@Service
public class OwnerRestaurantDetailService {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantCategoryRepository restaurantCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final StoreScheduleRepository storeScheduleRepository;
    private final S3Mover s3Mover;
    private final S3UrlService s3UrlService;

    public OwnerRestaurantDetailService(RestaurantRepository restaurantRepository,
                                        RestaurantCategoryRepository restaurantCategoryRepository,
                                        CategoryRepository categoryRepository,
                                        StoreScheduleRepository storeScheduleRepository,
                                        S3Mover s3Mover,
                                        S3UrlService s3UrlService) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantCategoryRepository = restaurantCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.storeScheduleRepository = storeScheduleRepository;
        this.s3Mover = s3Mover;
        this.s3UrlService = s3UrlService;
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

        //中間テーブルの取得
        List<RestaurantCategory> restaurantCategoryList = restaurantCategoryRepository.findByRestaurantId(restaurantId);

        //DTOに変換したデータを取得して、値を返す
        return RestaurantMapper.toRestaurantDetailHeader(restaurant, restaurantCategoryList, signedUrl);
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
        List<String> deleteAfterCommit = new ArrayList<>();

        if (hasText(requestDto.getExteriorTmpKey())) {
            String newKey = moveToFinal(requestDto.getExteriorTmpKey(), "exterior", restaurant.getId());
            String oldKey = restaurant.getImageUrl();
            restaurant.setImageUrl(newKey);
            if (hasText(oldKey)) deleteAfterCommit.add(oldKey);
            deleteAfterCommit.add(requestDto.getExteriorTmpKey());
        }
        //値を上書き
        Restaurant editRestaurant = RestaurantMapper.toEditRestaurantDetailHeader(restaurant, requestDto);
        restaurantRepository.save(editRestaurant);

        if (!deleteAfterCommit.isEmpty()) {
            org.springframework.transaction.support.TransactionSynchronizationManager
                    .registerSynchronization(new org.springframework.transaction.support.TransactionSynchronization() {
                        @Override public void afterCommit() {
                            try { s3Mover.deleteBatch(deleteAfterCommit); } catch (Exception ignored) {}
                        }
                    });
        }

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

    // 店舗削除
    @Transactional
    public void deleteRestaurant(UserDetails userDetails, UUID restaurantId) {
        //loginId取得
        String loginId = userDetails.getUsername();

        // 店舗Idから店舗取得
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(()->new RuntimeException("店舗を取得できませんでした"));

        // 削除対象のS3キーを事前に回収
        List<String> deleteAfterCommit = new ArrayList<>();
        if (hasText(restaurant.getImageUrl())) {
            deleteAfterCommit.add(restaurant.getImageUrl());
        }
        if (hasText(restaurant.getInteriorImageUrl())) {
            deleteAfterCommit.add(restaurant.getInteriorImageUrl());
        }

        // オーナー以外がアクセスした場合
        if (loginId == null || !loginId.equals(restaurant.getUser().getLoginId())) {
            throw new AccessDeniedException("この店舗にアクセスする権限がありません");
        }

        //店舗削除・中間テーブルの削除
        restaurantRepository.delete(restaurant);

        // コミット後にS3削除
        if (!deleteAfterCommit.isEmpty()) {
            org.springframework.transaction.support.TransactionSynchronizationManager
                    .registerSynchronization(new org.springframework.transaction.support.TransactionSynchronization() {
                        @Override public void afterCommit() {
                            try {
                                s3Mover.deleteBatch(deleteAfterCommit); // 個別ファイル
                            } catch (Exception ignored) {}
                            try {
                                // restaurants/{restaurantId}/ 以下を全削除
                                s3Mover.deletePrefix("restaurants/" + restaurantId + "/");
                            } catch (Exception ignored) {}
                        }
                    });
        }
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

        String signedUrl;
        String key = restaurant.getInteriorImageUrl();
        if (key == null || key.isBlank()) {
            signedUrl = "NO_IMAGE_URL";
        } else {
            try {
                signedUrl = s3UrlService.generatePresignedUrl(key);
            } catch (Exception e) {
                signedUrl = "NO_IMAGE_URL";
            }
        }
        // 定休日・営業時間を取得
        List<StoreSchedule> storeSchedules = storeScheduleRepository.findByRestaurant(restaurant);

        // DTOに変換したデータを取得して、値を返す
        return RestaurantMapper.toRestaurantDetailDto(restaurant, signedUrl, storeSchedules);
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

        List<String> deleteAfterCommit = new ArrayList<>();

        if (hasText(dto.getInteriorTmpKey())) {
            String newKey = moveToFinal(dto.getInteriorTmpKey(), "interior", restaurant.getId());
            String oldKey = restaurant.getInteriorImageUrl();
            restaurant.setInteriorImageUrl(newKey);
            if (hasText(oldKey)) deleteAfterCommit.add(oldKey);
            deleteAfterCommit.add(dto.getInteriorTmpKey());
        }

        restaurantRepository.save(restaurant); // 変更検知でもOK

        // ----- コミット後にまとめて削除（ロールバック安全） -----
        if (!deleteAfterCommit.isEmpty()) {
            org.springframework.transaction.support.TransactionSynchronizationManager
                    .registerSynchronization(new org.springframework.transaction.support.TransactionSynchronization() {
                        @Override public void afterCommit() {
                            try { s3Mover.deleteBatch(deleteAfterCommit); } catch (Exception ignored) {}
                        }
                    });
        }
    }

    // 店舗定休日・営業時間の編集
    @Transactional
    public void editRestaurantSchedule(UserDetails userDetails, UUID restaurantId, List<StoreScheduleDto> dto) {
        // loginId取得
        String loginId = userDetails.getUsername();

        // 店舗Idから店舗取得
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(()->new RuntimeException("店舗を取得できませんでした"));

        // オーナー以外がアクセスした場合
        if (loginId == null || !loginId.equals(restaurant.getUser().getLoginId())) {
            throw new AccessDeniedException("この店舗にアクセスする権限がありません");
        }

        // それぞれの曜日に値をセット
        List<StoreSchedule> storeScheduleList = dto.stream().map(storeScheduleDto -> {
            StoreSchedule storeSchedule = storeScheduleRepository.findById(storeScheduleDto.getId())
                    .orElseThrow(() -> new RuntimeException("該当するレコードはありません"));

            return RestaurantMapper.editSchedule(storeSchedule, storeScheduleDto);
        }).toList();

        // 定休日・営業時間をまとめて保存
        storeScheduleRepository.saveAll(storeScheduleList);
    }

    // 空文字・null・空白出ない場合値をセット
    private void applyIfHasText(String value, Consumer<String> setter) {
        if (hasText(value)) {
            setter.accept(value);
        }
    }

    private String moveToFinal(String tmpKeyMaybeUrl, String kind, UUID restaurantId) {
        // URLが来てもキーへ補正
        String srcKey = normalizeKey(tmpKeyMaybeUrl);

        // 最低限のバリデーション
        if (!srcKey.startsWith("restaurants/tmp/")) {
            throw new IllegalArgumentException("tmpキーの形式が不正です: " + srcKey);
        }

        String fileName = srcKey.substring(srcKey.lastIndexOf('/') + 1);
        String destKey  = "restaurants/%s/%s/%s".formatted(restaurantId, kind, fileName);

        // S3内部コピー（S3Moverは既存のものを使用）
        s3Mover.copy(srcKey, destKey);
        return destKey;
    }

    private String normalizeKey(String maybeUrl) {
        if (maybeUrl.startsWith("http")) {
            int i = maybeUrl.indexOf(".amazonaws.com/");
            if (i > 0) return maybeUrl.substring(i + ".amazonaws.com/".length());
        }
        return maybeUrl;
    }
}
