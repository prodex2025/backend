package com.backend.backend.app;

import com.backend.backend.domain.dto.LoginDto;
import com.backend.backend.domain.dto.OwnerRegisterDto;
import com.backend.backend.domain.dto.UserRegisterDto;
import com.backend.backend.domain.model.Role;
import com.backend.backend.domain.service.UserService;
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

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

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
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getLoginId(),
                            loginDto.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return ResponseEntity.ok("login成功");
    }

}
