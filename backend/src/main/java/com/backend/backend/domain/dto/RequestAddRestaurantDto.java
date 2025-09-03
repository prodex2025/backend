package com.backend.backend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestAddRestaurantDto {
    private String name;
    private String address;
    private String postCode;
    private String phone;
    private String email;
    private String description;
    private String exteriorTmpKey;
    private String interiorTmpKey;
    private String certificate;
    private List<StoreScheduleDto> storeScheduleDtoList;
    private List<CategoryDto> categoryDtoList;
}
