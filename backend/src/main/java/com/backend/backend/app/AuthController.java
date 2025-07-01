package com.backend.backend.app;

import com.backend.backend.domain.dto.LoginDto;
import com.backend.backend.domain.dto.OwnerRegisterDto;
import com.backend.backend.domain.dto.UserRegisterDto;
import com.backend.backend.domain.model.Role;
import com.backend.backend.domain.service.RefreshTokenService;
import com.backend.backend.domain.service.UserService;
import com.backend.backend.secuirty.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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

}
