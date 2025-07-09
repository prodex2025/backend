package com.backend.backend.app.controller;

import com.backend.backend.domain.dto.RestaurantCategoryDetailDto;
import com.backend.backend.domain.dto.RestaurantCategoryDto;
import com.backend.backend.domain.model.Restaurant;
import com.backend.backend.domain.model.RestaurantCategory;
import com.backend.backend.domain.repository.RestaurantCategoryRepository;
import com.backend.backend.domain.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantCategoryRepository restaurantCategoryRepository;

    //店舗詳細
    @GetMapping("/{roomId}")
    public ResponseEntity<?> test(@PathVariable UUID roomId) {
        Restaurant restaurant = restaurantRepository.findById(roomId).orElseThrow(()->new RuntimeException("店舗を取得できませんでした"));
        List<RestaurantCategory> categories = restaurantCategoryRepository.findByRestaurantId(roomId);
        //店舗詳細オブジェクトを作成
        RestaurantCategoryDetailDto restaurantCategoryDetailDto = new RestaurantCategoryDetailDto(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getPostCode(),
                categories.stream().map(RestaurantCategoryDto::fromEntity).toList()
        );
        return ResponseEntity.ok(restaurantCategoryDetailDto);
    }
    //公開
    @GetMapping("/true")
    public ResponseEntity<?> test1(@RequestParam(defaultValue = "0") int p) {
        Page<Restaurant> restaurants = restaurantRepository.findByIsPublishedTrue(PageRequest.of(p, 10, Sort.by(Sort.Direction.DESC, "createdAt")));
                return ResponseEntity.ok(restaurants);
    }
    //検索
    @GetMapping("/keyword")
    public ResponseEntity<?> test1(@RequestParam(defaultValue = "0") int p, @RequestParam String keyword) {
        Page<Restaurant> restaurants = restaurantRepository.findByNameContaining(PageRequest.of(p, 10, Sort.by(Sort.Direction.DESC, "createdAt")), keyword);
        return ResponseEntity.ok(restaurants);
    }
    //公開検索
    @GetMapping("/filter")
    public ResponseEntity<?> filter(@RequestParam(defaultValue = "0") int p, @RequestParam String keyword) {
        Page<Restaurant> restaurants = restaurantRepository.findByIsPublishedTrueAndNameContaining(PageRequest.of(p, 10, Sort.by(Sort.Direction.DESC, "createdAt")), keyword);
        return ResponseEntity.ok(restaurants);
    }
    //カテゴリ
    @GetMapping("/category")
    public ResponseEntity<?> category(@RequestParam(defaultValue = "0") int p,  @RequestParam List<UUID> categoryIds) {
        Page<Restaurant> restaurants = restaurantCategoryRepository.findRestaurantsByCategoryIds(categoryIds,(PageRequest.of(p, 10)));
        return ResponseEntity.ok(restaurants);
    }
    //カテゴリ検索
    @GetMapping("/category/keyword")
    public ResponseEntity<?> categoryKeyword(@RequestParam(defaultValue = "0") int p, @RequestParam List<UUID> categoryIds, @RequestParam String keyword) {
        //SQLでLikeが扱えるように
        String likeKeyword = "%" + keyword + "%";
        Page<Restaurant> restaurants = restaurantCategoryRepository.findRestaurantsByCategoryIdsAndKeyword(categoryIds,likeKeyword, PageRequest.of(p, 10));
        return ResponseEntity.ok(restaurants);
    }

}
