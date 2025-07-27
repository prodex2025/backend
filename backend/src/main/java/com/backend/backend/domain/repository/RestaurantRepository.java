package com.backend.backend.domain.repository;

import com.backend.backend.domain.model.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
    // 承認されたレストラン一覧を取得
    Page<Restaurant> findByApprovedTrue(Pageable pageable);

    // 承認されていないレストラン一覧を取得したい場合
    Page<Restaurant> findByApprovedFalse(Pageable pageable);

    // 公開されたレストラン一覧を取得
    Page<Restaurant> findByIsPublishedTrue(Pageable pageable);

    // 公開されていないレストラン一覧を取得したい場合
    Page<Restaurant> findByIsPublishedFalse(Pageable pageable);

    //店舗名の部分一致検索
    Page<Restaurant> findByNameContaining(Pageable pageable, String keyword);

    // 承認されていて、かつキーワード検索
    Page<Restaurant> findByApprovedTrueAndNameContaining(Pageable pageable, String keyword);

    // 未承認されていて、かつキーワード検索
    Page<Restaurant> findByApprovedFalseAndNameContaining(Pageable pageable, String keyword);

    // 公開されていて、かつキーワード検索
    Page<Restaurant> findByIsPublishedTrueAndNameContaining(Pageable pageable, String keyword);

    // 非公開されていて、かつキーワード検索（検索 + フィルタ）
    Page<Restaurant> findByIsPublishedFalseAndNameContaining(Pageable pageable, String keyword);

    //経営者登録店舗一覧
    Page<Restaurant> findByUserId(Pageable pageable, UUID userId);


}
