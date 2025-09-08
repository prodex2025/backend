package com.backend.backend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantCategoryDetailDto {
    private UUID id;
    private String name;
    private String address;
    private String postCode;
    private String imageUrl;
    private List<RestaurantCategoryDto> categoryDtoList;
}
