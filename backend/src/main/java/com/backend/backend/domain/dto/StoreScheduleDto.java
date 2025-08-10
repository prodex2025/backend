package com.backend.backend.domain.dto;

import com.backend.backend.domain.model.Restaurant;
import com.backend.backend.domain.model.RestaurantClosedDay;
import com.backend.backend.domain.model.StoreSchedule;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreScheduleDto {
    private UUID id;
    private Short dayOfWeek;
    private Boolean isClosed;
    private Time lunchStart;
    private Time lunchEnd;
    private Boolean isLunchClosed;
    private Time dinnerStart;
    private Time dinnerEnd;
    private Boolean isDinnerClosed;

    //複数取得するためのメソッド
    public static StoreScheduleDto fromEntity(StoreSchedule entity) {
        return new StoreScheduleDto(
                entity.getId(),
                entity.getDayOfWeek(),
                entity.getIsClosed(),
                entity.getLunchStart(),
                entity.getLunchEnd(),
                entity.getIsLunchClosed(),
                entity.getDinnerStart(),
                entity.getDinnerEnd(),
                entity.getIsDinnerClosed()
        );
    }

}
