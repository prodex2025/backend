package com.backend.backend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantBasicUpdateDto {
    private String address;
    private String phone;
    private String email;
    private String interiorTmpKey;;
}


