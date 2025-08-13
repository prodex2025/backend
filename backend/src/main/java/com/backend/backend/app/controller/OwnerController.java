package com.backend.backend.app.controller;

import com.backend.backend.domain.dto.*;
import com.backend.backend.domain.service.OwnerDishService;
import com.backend.backend.domain.service.OwnerRestaurantDetailService;
import com.backend.backend.domain.service.OwnerRestaurantService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/owner")
public class OwnerController {

    private final OwnerRestaurantService ownerRestaurantService;
    private final OwnerRestaurantDetailService ownerRestaurantDetailService;
    private final OwnerDishService ownerDishService;

    public OwnerController (
            OwnerRestaurantService ownerRestaurantService,
            OwnerRestaurantDetailService ownerRestaurantDetailService,
            OwnerDishService ownerDishService
    ) {
        this.ownerRestaurantService = ownerRestaurantService;
        this.ownerRestaurantDetailService = ownerRestaurantDetailService;
        this.ownerDishService = ownerDishService;
    }

    @GetMapping("/restaurants")
    public ResponseEntity<Page<OwnerRestaurantsDto>> getMyRestaurants(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(defaultValue = "0")int page) {
        //loginId取得
        String loginId = userDetails.getUsername();
        Page<OwnerRestaurantsDto> dtoPage = ownerRestaurantService.getMyRestaurants(loginId, page);
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

    @GetMapping("/restaurants/{restaurantId}/profile")
    public ResponseEntity<RestaurantDetailDto> getRestaurantDetail(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID restaurantId) {
        return ResponseEntity.ok(ownerRestaurantDetailService.getRestaurantDetail(userDetails, restaurantId));
    }

    @PutMapping("/restaurants/{restaurantId}/profile")
    public ResponseEntity<String> editRestaurantDetail(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID restaurantId, @RequestBody RestaurantBasicUpdateDto dto) {
        ownerRestaurantDetailService.editRestaurantDetailBasic(userDetails, restaurantId, dto);
        return ResponseEntity.ok("編集完了");
    }

    @PutMapping("/restaurants/{restaurantId}/profile/schedule")
    public ResponseEntity<String> editRestaurantDetailSchedule(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID restaurantId, @RequestBody List<StoreScheduleDto> dto) {
        ownerRestaurantDetailService.editRestaurantSchedule(userDetails, restaurantId, dto);
        return ResponseEntity.ok("編集完了");
    }

    @GetMapping("/restaurants/{restaurantId}/menus")
    public ResponseEntity<Page<DishesListDto>> getDishes(@AuthenticationPrincipal UserDetails userDetails,
                                                         @PathVariable UUID restaurantId,
                                                         @RequestParam(name = "page", defaultValue = "0") int page) {
        Page<DishesListDto> dishes = ownerDishService.getDishes(userDetails, restaurantId, page);
        return ResponseEntity.ok(dishes);
    }

    @PostMapping("/restaurants/{restaurantId}/menus")
    public ResponseEntity<String> addDish(@AuthenticationPrincipal UserDetails userDetails,
                                                  @PathVariable UUID restaurantId,
                                                  @RequestBody RequestDishDto dto) {
        ownerDishService.addDish(userDetails, restaurantId, dto);

        return ResponseEntity.ok("登録成功");
    }

    @PutMapping("/restaurants/{restaurantId}/menus/{menuId}")
    public ResponseEntity<String> editDish(@AuthenticationPrincipal UserDetails userDetails,
                                           @PathVariable UUID restaurantId,
                                           @PathVariable UUID menuId,
                                           @RequestBody RequestDishDto dto) {
        ownerDishService.editDish(userDetails, restaurantId, menuId, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body("登録成功");
    }

    @DeleteMapping("/restaurants/{restaurantId}/menus/{menuId}")
    public ResponseEntity<String> deleteDish(@AuthenticationPrincipal UserDetails userDetails,
                                             @PathVariable UUID restaurantId,
                                             @PathVariable UUID menuId) {
        ownerDishService.deleteDish(userDetails, restaurantId, menuId);

        return ResponseEntity.noContent().build();
    }

}
