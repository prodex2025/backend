package com.backend.backend.domain.repository;

import com.backend.backend.domain.model.RefreshToken;
import com.backend.backend.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    int deleteByExpiryDateBefore(Timestamp now);

    void deleteByUser(User user);
}
