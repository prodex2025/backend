package com.backend.backend.app.controller;

import com.backend.backend.domain.dto.*;
import com.backend.backend.domain.model.*;
import com.backend.backend.domain.repository.*;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private RestaurantBusinessHoursRepository restaurantBusinessHoursRepository;

    @Autowired
    private RestaurantClosedDayRepository restaurantClosedDayRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private DishAllergyRepository dishAllergyRepository;

    //店舗詳細
    @GetMapping("/{restaurantId}")
    public ResponseEntity<?> test(@PathVariable UUID restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(()->new RuntimeException("店舗を取得できませんでした"));
        List<RestaurantCategory> categories = restaurantCategoryRepository.findByRestaurantId(restaurantId);
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
    //店舗詳細タブ一覧
    @GetMapping("/{restaurantId}/detail")
    public ResponseEntity<?> restaurantDetail(@PathVariable UUID restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(()->new RuntimeException("店舗を取得できませんでした"));
        List<RestaurantBusinessHours> businessHours = restaurantBusinessHoursRepository.findByRestaurantId(restaurantId);
        List<RestaurantClosedDay> closedDays = restaurantClosedDayRepository.findByRestaurantId(restaurantId);
        RestaurantDetailDto restaurantDetailDto = new RestaurantDetailDto(
                restaurant.getId(),
                restaurant.getAddress(),
                restaurant.getPhone(),
                restaurant.getDescription(),
                restaurant.getInteriorImageUrl(),
                businessHours.stream().map(BusinessHoursDto::fromEntity).toList(),
                closedDays.stream().map(ClosedDayDto::fromEntity).toList()
        );
        return ResponseEntity.ok(restaurantDetailDto);
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

    @GetMapping("/{restaurantId}/dishes")
    public ResponseEntity<?> findByDishList(@RequestParam(defaultValue = "0") int p, @PathVariable UUID restaurantId) {
        Page<Dish> dishesList = dishRepository.findByRestaurantId(PageRequest.of(p, 10), restaurantId);
        Page<DishesListDto> dtoPage = dishesList.map(dish -> new DishesListDto(
                dish.getId(),
                dish.getName(),
                dish.getPrice(),
                dish.getImageUrl()
        ));
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("dishes/{dishesId}")
    public ResponseEntity<?> findByDish(@PathVariable UUID dishesId) {
        Dish dish = dishRepository.findById(dishesId).orElseThrow(()-> new RuntimeException("値を取得できませんでした"));
        List<DishAllergy> dishAllergy = dishAllergyRepository.findByDishId(dishesId);
        Dish3dDto dish3dDto = new Dish3dDto(
                dish.getId(),
                dish.getName(),
                dish.getVideoUrl(),
                dish.getDescription(),
                dishAllergy.stream().map(AllergyDto::fromEntity).toList()
        );
        return ResponseEntity.ok(dish3dDto);
    }

    @GetMapping("owner/restaurant")
    public ResponseEntity<?> OwnerRestaurants(@RequestParam(defaultValue = "0") int p, @RequestParam UUID userId) {
        //経営者の登録店舗を取得
        Page<Restaurant> restaurants = restaurantRepository.findByUserId(PageRequest.of(p, 10), userId);

        Page<OwnerRestaurantsDto> dtoPage = restaurants.map(restaurant -> {
            // 中間テーブルからカテゴリを取得
            List<RestaurantCategory> restaurantCategories = restaurantCategoryRepository.findByRestaurantId(restaurant.getId());
            //カテゴリを取得
            List<RestaurantCategoryDto> categoryList = restaurantCategories.stream()
                    .map(RestaurantCategoryDto::fromEntity)
                    .toList();

            return new OwnerRestaurantsDto(
                    restaurant.getId(),
                    restaurant.getName(),
                    restaurant.getAddress(),
                    restaurant.getPostCode(),
                    restaurant.getImageUrl(),
                    categoryList
            );
        });
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/owner/{restaurantId}/dishes")
    public ResponseEntity<?> findByOwnerDishList(@RequestParam(defaultValue = "0") int p, @PathVariable UUID restaurantId) {
        Page<Dish> OwnerDishesListDto = dishRepository.findByRestaurantId(PageRequest.of(p, 10), restaurantId);
        Page<OwnerDishesListDto> dtoPage = OwnerDishesListDto.map(dish -> {
            // 中間テーブルからカテゴリを取得
            List<DishAllergy> dishAllergies = dishAllergyRepository.findByDishId(dish.getId());
            //カテゴリを取得
            List<AllergyDto> allergyDtoList= dishAllergies.stream().map(AllergyDto::fromEntity)
                    .toList();
            return new OwnerDishesListDto(
                    dish.getId(),
                    dish.getName(),
                    dish.getPrice(),
                    dish.getDescription(),
                    dish.getImageUrl(),
                    dish.getVideoUrl(),
                    allergyDtoList
            );
        });

        return ResponseEntity.ok(dtoPage);
    }

}
