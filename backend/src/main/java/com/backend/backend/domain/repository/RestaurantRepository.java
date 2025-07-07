package com.backend.backend.domain.repository;

import com.backend.backend.domain.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
    // 承認されたレストラン一覧を取得
    List<Restaurant> findByApprovedTrue();

    // 承認されていないレストラン一覧を取得したい場合はこっち
    List<Restaurant> findByApprovedFalse();

    // 公開されたレストラン一覧を取得
    List<Restaurant> findByIsPublishedTrue();

    // 公開されていないレストラン一覧を取得したい場合はこっち
    List<Restaurant> findByIsPublishedFalse();
}
