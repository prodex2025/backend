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
    private String description;
    private List<BusinessHoursDto> businessHoursDtoList;
    private List<ClosedDayDto> closedDayDtoList;
}
