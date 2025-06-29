package com.backend.backend.app;

import com.backend.backend.domain.dto.OwnerRegisterDto;
import com.backend.backend.domain.dto.UserRegisterDto;
import com.backend.backend.domain.model.Role;
import com.backend.backend.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

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

}
