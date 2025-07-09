package com.backend.backend.domain.dto;

import com.backend.backend.domain.model.RestaurantCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantCategoryDto {
    private UUID id;
    private String name;

    //カテゴリ複数習得するための静的メソッド
    public static RestaurantCategoryDto fromEntity(RestaurantCategory entity) {
        return new RestaurantCategoryDto(
                entity.getCategory().getId(),
                entity.getCategory().getName()
        );
    }

}
