package com.backend.backend.domain.service.specification;

import com.backend.backend.app.status.StatusFilter;
import com.backend.backend.domain.model.Restaurant;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public final class RestaurantSpecs {

    // keywordのクエリを作成
    public static Specification<Restaurant> keywordLike(String keyword) {
        return (root, q, cb) -> {
            // 空の場合条件なしで返す
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }
            String like = "%" + keyword.trim() + "%";
            return cb.or(
                    cb.like(root.get("name"), like) // nameでkeyword検索
            );
        };
    }
    // カテゴリのクエリを作成
    public static Specification<Restaurant> categoryIn(List<UUID> categoryIds) {
        return (root, q, cb) -> {
            // 空の場合条件なしで返す
            if (categoryIds == null || categoryIds.isEmpty()) {
                return cb.conjunction();
            }
            var rc = root.join("restaurantCategories", JoinType.LEFT); // 中間をJOIN
            if (q != null) {
                q.distinct(true); // 重複チェック
            }
            return rc.get("category").get("id").in(categoryIds); // 複数のカテゴリを検索
        };
    }

    // タブごとのクエリを作成
    public static Specification<Restaurant> status(StatusFilter status) {
        return (root, q, cb) -> {
            // タブごとに切り換え
            return switch (status) {
                case APPROVED     -> cb.isTrue(root.get("approved"));
                case NOT_APPROVED -> cb.isFalse(root.get("approved"));
                case PUBLISHED    -> cb.isTrue(root.get("isPublished"));
                case UNPUBLISHED  -> cb.isFalse(root.get("isPublished"));
            };
        };
    }
}
