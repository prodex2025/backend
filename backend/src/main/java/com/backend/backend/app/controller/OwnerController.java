package com.backend.backend.app.controller;

import com.backend.backend.domain.dto.OwnerRestaurantsDto;
import com.backend.backend.domain.dto.RestaurantCategoryDto;
import com.backend.backend.domain.dto.RestaurantDetailDto;
import com.backend.backend.domain.model.Restaurant;
import com.backend.backend.domain.model.RestaurantCategory;
import com.backend.backend.domain.model.User;
import com.backend.backend.domain.repository.RestaurantCategoryRepository;
import com.backend.backend.domain.repository.RestaurantRepository;
import com.backend.backend.domain.repository.UserRepository;
import com.backend.backend.domain.service.OwnerRestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
