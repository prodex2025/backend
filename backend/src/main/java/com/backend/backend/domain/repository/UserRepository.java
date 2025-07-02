package com.backend.backend.domain.repository;

import com.backend.backend.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    //loginIdを基にUserを取得
    Optional<User> findByLoginId(String loginId);
    //loginIdが正しいかチェック
    boolean existsByLoginId(String loginId);
}
