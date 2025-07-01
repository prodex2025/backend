package com.backend.backend.app;

import com.backend.backend.domain.dto.LoginDto;
import com.backend.backend.domain.dto.OwnerRegisterDto;
import com.backend.backend.domain.dto.RefreshTokenRequestDto;
import com.backend.backend.domain.dto.UserRegisterDto;
import com.backend.backend.domain.model.RefreshToken;
import com.backend.backend.domain.model.Role;
import com.backend.backend.domain.model.User;
import com.backend.backend.domain.repository.RefreshTokenRepository;
import com.backend.backend.domain.service.RefreshTokenService;
import com.backend.backend.domain.service.UserService;
import com.backend.backend.secuirty.CustomUserDetailsService;
import com.backend.backend.secuirty.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    //ログイン状態を確認
    @GetMapping("/status")
    public ResponseEntity<?> checkLoginStatus(Authentication authentication) {
        //ログイン済みUserかを判定
        boolean isAuthenticated = authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken);
        if (isAuthenticated) {
            return ResponseEntity.ok(Map.of(
                    "authenticated", true,
                    "username", authentication.getName()
            ));
        } else {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }
    }

    //利用者新規登録
    @PostMapping
    public ResponseEntity<String> Register(@RequestBody UserRegisterDto userRegisterDto) {
        //同じloginIdがないかチェック
        if (userService.existsByLoginId(userRegisterDto.getLoginId())) {
            return ResponseEntity.badRequest().body("既に存在するloginIdです");
        }
        //ユーザ登録
        userService.register(userRegisterDto, Role.ROLE_USER);

        return ResponseEntity.ok("登録が完了しました");
    }

    //経営者新規登録
    @PostMapping("/owner")
    public ResponseEntity<String> ownerRegister(@RequestBody OwnerRegisterDto ownerRegisterDto) {
        //同じloginIdがないかチェック
        if (userService.existsByLoginId(ownerRegisterDto.getLoginId())) {
            return ResponseEntity.badRequest().body("既に存在するloginIdです");
        }
        //ユーザ登録
        userService.register(ownerRegisterDto, Role.ROLE_OWNER);

        return ResponseEntity.ok("登録が完了しました");
    }

    //ログイン認証
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getLoginId(),
                            loginDto.getPassword()
                    )
            );
            //認証済みUserをセット
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //認証済みUserをUserDetails型に変換してuserDetailsに代入
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            //アクセストークン発行
            String accessToken = jwtService.generateAccessToken(userDetails);
            //リフレッシュトークン発行
            String refreshToken = jwtService.generateRefreshToken(userDetails);
            //リフレッシュトークをDBに保存
            refreshTokenService.saveRefreshToken(refreshToken, userDetails);

            return ResponseEntity.ok(Map.of("accessToken", accessToken,
                    "refreshToken", refreshToken));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ログイン失敗：ユーザーが見つかりません");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ログイン失敗：IDまたはパスワードが間違っています");
        }
    }

    //リフレッシュトークを使用し、アクセストークンを作成
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.
                findByToken(request.getRefreshToken());
        //トークンが存在しない場合
        if (refreshTokenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("無効なリフレッシュトークン");
        }
        //値を取得
        RefreshToken refreshToken = refreshTokenOptional.get();
        //認証Userがトークン所有者と一致するか
        String currentLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!refreshToken.getUser().getLoginId().equals(currentLoginId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("他人のトークンを操作することはできません");
        }
        //有効期限をチェック
        if (refreshToken.getExpiryDate().before(new Timestamp(System.currentTimeMillis()))) {
            //期限切れトークンを削除
            refreshTokenRepository.delete(refreshToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("リフレッシュトークンが期限切れ");
        }
        //アクセストークンを再発行
        User user = refreshToken.getUser();
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getLoginId());
        //新しいトークンを作成
        String newAccessToken = jwtService.generateAccessToken(userDetails);

        //新しいアクセストークンを返す
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    //logoutトークン削除
    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequestDto request) {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.
                findByToken(request.getRefreshToken());
        //トークンが存在しない場合
        if (refreshTokenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("無効なリフレッシュトークン");
        }
        //値を取得
        RefreshToken refreshToken = refreshTokenOptional.get();
        //認証Userがトークン所有者と一致するか
        String currentLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!refreshToken.getUser().getLoginId().equals(currentLoginId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("他人のトークンを削除することはできません");
        }
        //refreshTokenを削除
        refreshTokenRepository.delete(refreshToken);

        return ResponseEntity.ok("tokenを削除しました。");
    }

}
