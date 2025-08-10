package com.backend.backend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDetailDto {
    private UUID id;
    private String address;
    private String phone;
    private String email;
    private String description;
    private String interiorImageUrl;
    private List<StoreScheduleDto> storeScheduleDtoList;
}
