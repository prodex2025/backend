package com.backend.backend.domain.service.mapper;

import com.backend.backend.domain.dto.DishesListDto;
import com.backend.backend.domain.model.Dish;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.Optional;

public class DishMapper {
    //料理DTO返却
    public static Page<DishesListDto> toDishesListDtoPage(Page<Dish> dishes) {
        return dishes.map(dish -> new DishesListDto(
                dish.getId(),
                dish.getName(),
                dish.getPrice(),
                dish.getImageUrl()
        ));
    }
}
