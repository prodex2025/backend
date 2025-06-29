package com.backend.backend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDto implements BaseRegisterDto {
    private String loginId;
    private String password;

    @Override
    public String getUserName() {
        return null; // 利用者はユーザー名なしでOK
    }
}
