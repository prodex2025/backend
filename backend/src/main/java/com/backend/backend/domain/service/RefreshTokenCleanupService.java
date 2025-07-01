package com.backend.backend.domain.service;

import com.backend.backend.domain.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class RefreshTokenCleanupService {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 3 * * *") // 毎日深夜3時
    public void deleteExpiredTokens() {
        Timestamp now = Timestamp.from(Instant.now());
        int deleted = refreshTokenRepository.deleteByExpiryDateBefore(now);
        System.out.println("削除された期限切れトークン数: " + deleted);
    }
}
