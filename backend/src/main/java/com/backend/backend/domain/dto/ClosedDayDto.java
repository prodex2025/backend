package com.backend.backend.domain.dto;

import com.backend.backend.domain.model.RestaurantBusinessHours;
import com.backend.backend.domain.model.RestaurantClosedDay;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClosedDayDto {
    private UUID id;
    private Short dayOfWeek;

    //複数の定休日を取得するためのメソッド
    public static ClosedDayDto fromEntity(RestaurantClosedDay entity) {
        return new ClosedDayDto(
                entity.getId(),
                entity.getDayOfWeek()
        );
    }
}
