package com.backend.backend.domain.repository;

import com.backend.backend.domain.model.Restaurant;
import com.backend.backend.domain.model.RestaurantCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RestaurantCategoryRepository extends JpaRepository<RestaurantCategory, UUID> {
    //カテゴリごとに表示
    @Query("""
            SELECT DISTINCT rc.restaurant FROM RestaurantCategory rc
            WHERE rc.category.id IN :categoryIds
            ORDER BY rc.restaurant.createdAt DESC
            """)
    Page<Restaurant>findRestaurantsByCategoryIds(@Param("categoryIds") List<UUID> categoryIds, Pageable pageable);

    //カテゴリ選択&検索
    @Query("""
    SELECT DISTINCT rc.restaurant FROM RestaurantCategory rc
    WHERE rc.category.id IN :categoryIds
    AND rc.restaurant.name LIKE :keyword
    ORDER BY rc.restaurant.createdAt DESC
    """)
    Page<Restaurant> findRestaurantsByCategoryIdsAndKeyword(
            @Param("categoryIds") List<UUID> categoryIds,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    List<RestaurantCategory> findByRestaurantId(UUID roomId);
}
