package com.backend.backend.domain.dto;

import com.backend.backend.domain.model.RestaurantBusinessHours;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessHoursDto {
    private Short dayOfWeek;
    private Time openTime;
    private Time closeTime;

    //複数の営業時間を取得するためのメソッド
    public static BusinessHoursDto fromEntity(RestaurantBusinessHours entity) {
        return new BusinessHoursDto(
                entity.getDayOfWeek(),
                entity.getOpenTime(),
                entity.getCloseTime()
        );
    }
}
