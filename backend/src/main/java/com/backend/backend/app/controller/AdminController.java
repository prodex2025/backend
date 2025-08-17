package com.backend.backend.app.controller;

import com.backend.backend.app.status.StatusFilter;
import com.backend.backend.domain.dto.*;
import com.backend.backend.domain.service.AdminCategoryService;
import com.backend.backend.domain.service.AdminRestaurantService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminRestaurantService adminRestaurantService;
    private final AdminCategoryService adminCategoryService;

    public AdminController(AdminRestaurantService adminRestaurantService,
                           AdminCategoryService adminCategoryService) {
        this.adminRestaurantService = adminRestaurantService;
        this.adminCategoryService = adminCategoryService;
    }

    @GetMapping("/restaurants")
    public Page<AdminRestaurantDto> getRestaurantsList(@AuthenticationPrincipal UserDetails userDetails,
                                                       @RequestParam(defaultValue = "0")int page,
                                                       @RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) List<UUID> categoryIds,
                                                       @RequestParam(required = false, defaultValue = "APPROVED") StatusFilter status) {
        return adminRestaurantService.getRestaurants(page, keyword, categoryIds, status);
    }

    @PutMapping("/restaurants/{restaurantId}/isPublished")
    public ResponseEntity<String> editIsPublished(@AuthenticationPrincipal UserDetails userDetails,
                                                  @PathVariable UUID restaurantId,
                                                  @RequestBody AdminRequestIsPublishedDto dto) {
        adminRestaurantService.editIsPublished(userDetails, restaurantId, dto);

        return ResponseEntity.ok("編集完了");
    }

    @PutMapping("/restaurants/{restaurantId}/approved")
    public ResponseEntity<String> editApproved(@AuthenticationPrincipal UserDetails userDetails,
                                                  @PathVariable UUID restaurantId,
                                                  @RequestBody AdminRequestApprovedDto dto) {
        adminRestaurantService.editApproved(userDetails, restaurantId, dto);

        return ResponseEntity.ok("編集完了");
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getCategories(@AuthenticationPrincipal UserDetails userDetails) {
        List<CategoryDto> categoryList =  adminCategoryService.getCategories(userDetails);

        return ResponseEntity.ok(categoryList);
    }

    @PostMapping("/categories")
    public ResponseEntity<String> addCategory(@AuthenticationPrincipal UserDetails userDetails, @RequestBody RequestCategoryDto dto) {
        adminCategoryService.addCategory(userDetails, dto);

        return ResponseEntity.ok("追加完了");
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID categoryId) {
        adminCategoryService.deleteCategory(userDetails, categoryId);

        return ResponseEntity.ok("削除完了");
    }

}
