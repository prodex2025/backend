package com.backend.backend.domain.service;

import com.backend.backend.app.status.StatusFilter;
import com.backend.backend.domain.dto.AdminRequestApprovedDto;
import com.backend.backend.domain.dto.AdminRequestIsPublishedDto;
import com.backend.backend.domain.dto.AdminRestaurantDto;
import com.backend.backend.domain.model.Restaurant;
import com.backend.backend.domain.model.Role;
import com.backend.backend.domain.model.User;
import com.backend.backend.domain.repository.RestaurantRepository;
import com.backend.backend.domain.repository.UserRepository;
import com.backend.backend.domain.service.specification.RestaurantSpecs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AdminRestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public AdminRestaurantService(RestaurantRepository restaurantRepository,
                                  UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
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

    // 公開・非公開の更新
    @Transactional
    public void editIsPublished(UserDetails userDetails, UUID restaurantId, AdminRequestIsPublishedDto dto) {
        // loginId取得
        String loginId = userDetails.getUsername();
        // 店舗Idから店舗取得
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(()->new RuntimeException("店舗を取得できませんでした"));

        User user = userRepository.findByLoginId(loginId).orElseThrow(()-> new RuntimeException("Userを取得できませんでした"));
        // 管理者以外がアクセスした場合
        if (loginId == null || !user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new AccessDeniedException("権限がありません");
        }
        // 更新する値をセット
        restaurant.setIsPublished(dto.getIsPublished());
        restaurantRepository.save(restaurant);
    }

    // 公開・非公開の更新
    @Transactional
    public void editApproved(UserDetails userDetails, UUID restaurantId, AdminRequestApprovedDto dto) {
        // loginId取得
        String loginId = userDetails.getUsername();
        // 店舗Idから店舗取得
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(()->new RuntimeException("店舗を取得できませんでした"));

        User user = userRepository.findByLoginId(loginId).orElseThrow(()-> new RuntimeException("Userを取得できませんでした"));
        // 管理者以外がアクセスした場合
        if (loginId == null || !user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new AccessDeniedException("権限がありません");
        }
        // 更新する値をセット
        restaurant.setApproved(dto.getApproved());
        restaurantRepository.save(restaurant);
    }

}
