package com.backend.backend.app.controller;

import com.backend.backend.domain.dto.OwnerRestaurantsDto;
import com.backend.backend.domain.dto.RequestAddRestaurantDto;
import com.backend.backend.domain.service.OwnerRestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/owner")
public class OwnerController {

    @Autowired
    private OwnerRestaurantService ownerRestaurantService;

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

}
