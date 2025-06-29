package com.backend.backend.domain.service;

import com.backend.backend.domain.dto.BaseRegisterDto;
import com.backend.backend.domain.dto.OwnerRegisterDto;
import com.backend.backend.domain.dto.UserRegisterDto;
import com.backend.backend.domain.model.Role;
import com.backend.backend.domain.model.User;
import com.backend.backend.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //新規登録
    @Transactional
    public void register(BaseRegisterDto dto, Role role) {
        try {
            User user = new User();
            user.setLoginId(dto.getLoginId());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            // userNameがnullまたは空なら "user" を使う（利用者用）
            String name = (dto.getUserName() == null || dto.getUserName().isBlank())
                    ? "user"
                    : dto.getUserName();
            user.setUserName(name);
            user.setRole(role);
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("ユーザ登録に失敗しました", e);
        }
    }

    //loginId重複チェック
    public boolean existsByLoginId(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

}
