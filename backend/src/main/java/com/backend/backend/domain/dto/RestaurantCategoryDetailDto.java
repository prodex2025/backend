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
    private UUID restaurantId;
    private String restaurantName;
    private String restaurantAddress;
    private String restaurantPostCode;
    private List<RestaurantCategoryDto> categoryDtoList;
}
