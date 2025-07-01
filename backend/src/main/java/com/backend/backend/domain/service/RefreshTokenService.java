package com.backend.backend.domain.service;

import com.backend.backend.domain.model.RefreshToken;
import com.backend.backend.domain.model.User;
import com.backend.backend.domain.repository.RefreshTokenRepository;
import com.backend.backend.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class RefreshTokenService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    //リフレッシュトークを保存
    public void saveRefreshToken(String refreshTokenStr, UserDetails userDetails) {
        //Userが存在するか
        User user = userRepository.findByLoginId(userDetails.getUsername()).orElseThrow(()-> new RuntimeException("Userが存在しません"));
        //リフレッシュトークオブジェクトを生成
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(refreshTokenStr);
        refreshToken.setExpiryDate(Timestamp.from(Instant.now().plus(7, ChronoUnit.DAYS)));  //7日後有効期限
        //保存処理
        refreshTokenRepository.save(refreshToken);
    }
}
