package com.backend.backend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestEditRestaurantHeaderDto {
    private String restaurantName;
    private String restaurantAddress;
    private String restaurantPostCode;
    private List<RestaurantCategoryDto> categoryDtoList;
}
