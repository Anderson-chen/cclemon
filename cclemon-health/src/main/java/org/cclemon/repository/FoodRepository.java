package org.cclemon.repository;

import org.cclemon.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Long> {

    List<Food> findByDeletedFalse();

    List<Food> findByUser_IdIsNullAndDeletedFalse();
}
