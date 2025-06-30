package com.backend.backend.secuirty;

import com.backend.backend.domain.model.User;
import com.backend.backend.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //usernameからUser情報を取得
        User user = userRepository.findByLoginId(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        //権限として設定
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());
        //UserDetailsのインスタンスを生成
        return org.springframework.security.core.userdetails.User
                //loginIdをセット
                .withUsername(user.getLoginId())
                //passwordをセット
                .password(user.getPassword())
                .authorities(authority)
                .build();
    }
}
