package com.backend.backend.app.controller;

import com.backend.backend.domain.dto.*;
import com.backend.backend.domain.service.OwnerDishService;
import com.backend.backend.domain.service.OwnerRestaurantDetailService;
import com.backend.backend.domain.service.OwnerRestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/owner")
public class OwnerController {

    @Autowired
    private OwnerRestaurantService ownerRestaurantService;

    @Autowired
    private OwnerRestaurantDetailService ownerRestaurantDetailService;

    @Autowired
    private OwnerDishService  ownerDishService;

    @GetMapping("/restaurants")
    public ResponseEntity<Page<OwnerRestaurantsDto>> getMyRestaurants(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(defaultValue = "0")int p) {
        //loginId取得
        String loginId = userDetails.getUsername();
        Page<OwnerRestaurantsDto> dtoPage = ownerRestaurantService.getMyRestaurants(loginId, p);
        return ResponseEntity.ok(dtoPage);
    }

    @PostMapping("/restaurants")
    public ResponseEntity<String> addMyRestaurant(@AuthenticationPrincipal UserDetails userDetails, @RequestBody RequestAddRestaurantDto requestAddRestaurantDto) {
        //loginId取得
        String loginId = userDetails.getUsername();
        ownerRestaurantService.addMyRestaurant(requestAddRestaurantDto, loginId);
        return ResponseEntity.ok("登録完了");
    }

    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<RestaurantCategoryDetailDto> getMyRestaurantHeader(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID restaurantId) {
        //値を取得
        RestaurantCategoryDetailDto restaurantDetailHeader = ownerRestaurantDetailService.getMyRestaurantHeader(userDetails, restaurantId);
        return ResponseEntity.ok(restaurantDetailHeader);
    }

    @PutMapping("/restaurants/{restaurantId}")
    public ResponseEntity<String> editRestaurantHeader(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID restaurantId, @RequestBody RequestEditRestaurantHeaderDto requestDto) {
        ownerRestaurantDetailService.editRestaurantHeader(userDetails, restaurantId, requestDto);
        return ResponseEntity.ok("編集完了");
    }

    @DeleteMapping("/restaurants/{restaurantId}")
    public ResponseEntity<String> deleteRestaurant(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID restaurantId) {
        ownerRestaurantDetailService.deleteRestaurant(userDetails, restaurantId);
        return ResponseEntity.ok("削除完了");
    }

    @GetMapping("/restaurants/{restaurantId}/menus")
    public ResponseEntity<Page<DishesListDto>> getDishes(@AuthenticationPrincipal UserDetails userDetails,
                                                         @PathVariable UUID restaurantId,
                                                         @RequestParam(name = "page", defaultValue = "0") int page) {
        Page<DishesListDto> dishes = ownerDishService.getDishes(userDetails, restaurantId, page);
        return ResponseEntity.ok(dishes);
    }

}
