package com.backend.backend.domain.service;

import com.backend.backend.app.status.StatusFilter;
import com.backend.backend.domain.dto.AdminRestaurantDto;
import com.backend.backend.domain.model.Restaurant;
import com.backend.backend.domain.repository.RestaurantRepository;
import com.backend.backend.domain.service.specification.RestaurantSpecs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AdminRestaurantService {

    private final RestaurantRepository restaurantRepository;

    public AdminRestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public Page<AdminRestaurantDto> getRestaurants(
            int page,
            String keyword,
            List<UUID> categoryIds,
            StatusFilter status
    ) {
        // リクエストを基にクエリを作成
        Specification<Restaurant> spec = Specification.allOf(
                RestaurantSpecs.keywordLike(keyword),
                RestaurantSpecs.categoryIn(categoryIds),
                RestaurantSpecs.status(status)
        );

        int size = 10; // 1ページの取得するサイズ
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Restaurant> pageData = restaurantRepository.findAll(spec, pageable);
        // 取得したデータをDTOにセットして値を返す
        return pageData.map(AdminRestaurantDto::from);
    }
}
