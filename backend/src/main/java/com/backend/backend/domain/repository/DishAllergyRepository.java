package com.backend.backend.domain.repository;

import com.backend.backend.domain.model.Dish;
import com.backend.backend.domain.model.DishAllergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DishAllergyRepository extends JpaRepository<DishAllergy, UUID> {
    List<DishAllergy> findByDishId(UUID dishesId);

    // 引数の料理が含まれるモノをすべて削除
    void deleteByDish(Dish dish);
}
