package com.backend.backend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OwnerRegisterDto implements BaseRegisterDto {
    private String loginId;
    private String password;
    private String userName;
}
