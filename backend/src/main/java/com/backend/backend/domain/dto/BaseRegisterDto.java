package com.backend.backend.domain.dto;

public interface BaseRegisterDto {
    String getLoginId();
    String getPassword();
    String getUserName(); // 利用者は null または "" でOK
}
