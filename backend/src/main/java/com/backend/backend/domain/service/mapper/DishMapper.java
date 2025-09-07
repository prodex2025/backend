package com.backend.backend.domain.service.mapper;

import com.backend.backend.domain.dto.AllergyDto;
import com.backend.backend.domain.dto.Dish3dDto;
import com.backend.backend.domain.dto.DishesListDto;
import com.backend.backend.domain.dto.RequestDishDto;
import com.backend.backend.domain.model.Allergy;
import com.backend.backend.domain.model.Dish;
import com.backend.backend.domain.model.DishAllergy;
import com.backend.backend.domain.model.Restaurant;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public class DishMapper {
    // 料理DTO返却
    public static Page<DishesListDto> toDishesListDtoPage(Page<Dish> dishes) {
        return dishes.map(dish -> new DishesListDto(
                dish.getId(),
                dish.getName(),
                dish.getPrice(),
                dish.getImageUrl()
        ));
    }

    public static Dish3dDto toDish3dDto(Dish dish) {
        Dish3dDto dto = new Dish3dDto();
        dto.setId(dish.getId());
        dto.setName(dish.getName());
        dto.setVideoUrl(dish.getVideoUrl());
        dto.setDescription(dish.getDescription());

        dto.setAllergyDtoList(
                dish.getDishAllergies().stream()
                        .map(AllergyDto::fromEntity)
                        .toList()
        );

        return dto;
    }

    // DTOからDishに値をセット
    public static Dish toDishEntity(RequestDishDto dto, Restaurant restaurant) {
        Dish dish = new Dish();
        dish.setRestaurant(restaurant);
        dish.setName(dto.getName());
        dish.setPrice(dto.getPrice());
        dish.setDescription(dto.getDescription());
        dish.setImageUrl("PENDING");
        dish.setVideoUrl("PENDING");

        return dish;
    }

    // 料理とアレルギーの中間テーブルに値をセット
    public static DishAllergy toDishAllergy(Dish dish, Allergy allergy) {
        DishAllergy dishAllergy = new DishAllergy();
        dishAllergy.setDish(dish);
        dishAllergy.setAllergy(allergy);

        return dishAllergy;
    }

    // 料理編集値をセット
    public static Dish setEditValues(Dish dish, RequestDishDto dto) {
        dish.setName(dto.getName());
        dish.setPrice(dto.getPrice());
        dish.setDescription(dto.getDescription());
        return dish;
    }
}
