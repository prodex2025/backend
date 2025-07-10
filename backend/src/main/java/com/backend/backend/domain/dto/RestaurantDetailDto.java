package com.backend.backend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDetailDto {
    private String address;
    private String phone;
    private String description;
    private List<BusinessHoursDto> businessHoursDtoList;
    private List<ClosedDayDto> closedDayDtoList;
}
