package com.backend.backend.domain.dto;

import com.backend.backend.domain.model.DishAllergy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllergyDto {
    private UUID id;
    private String name;

    //アレルギー複数取得するためのメソッド
    public static AllergyDto fromEntity(DishAllergy entity) {
        return new AllergyDto(
                entity.getAllergy().getId(),
                entity.getAllergy().getName()
        );
    }

}
