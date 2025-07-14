package com.backend.backend.secuirty;

import com.backend.backend.domain.model.User;
import com.backend.backend.domain.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    private final Key SECRET_KEY;

    @Autowired
    private UserRepository userRepository;
    @Value("${jwt.access.expiration}")
    private long accessTokenExpirationMs;
    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpirationMs;

    //秘密鍵生成
    public JwtService(@Value("${JWT_SECRET}") String secret) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // アクセストークン発行
    public String generateAccessToken(UserDetails userDetails) {
        User user = userRepository.findByLoginId(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("userが取得できませんでした"));
        return Jwts.builder()
                .setSubject(userDetails.getUsername()) //トークンが誰のモノか
                .claim("role", user.getRole().name()) //Role情報を追加
                .setIssuedAt(new Date()) // 発行時間
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs)) // 有効期限
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)// 署名
                .compact(); //JWTを構築
    }

    //リフレッシュトークン発行
    public String generateRefreshToken(UserDetails userDetails) {
        User user = userRepository.findByLoginId(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("userが取得できませんでした"));
        return Jwts.builder()
                .setSubject(userDetails.getUsername()) //トークンが誰のモノか
                .claim("role", user.getRole().name()) //Role情報を追加
                .setIssuedAt(new Date()) // 発行時間
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs)) // 有効期限
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)// 署名
                .compact(); //JWTを構築
    }

    // トークンからログインID（=subject）を取り出す
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // トークンの有効性を確認
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // 有効期限切れか確認
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // JWTの全claimを取り出す
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()        //パースするためのビルダーを準備
                    .setSigningKey(SECRET_KEY) //署名用の鍵をセット
                    .build()                   //実行可能なparserを完成
                    .parseClaimsJws(token)     //トークンの検証・解析
                    .getBody();                //claimを取り出す
        } catch (JwtException e) {
            throw new IllegalStateException("無効なトークンです", e);
        }

    }

}
