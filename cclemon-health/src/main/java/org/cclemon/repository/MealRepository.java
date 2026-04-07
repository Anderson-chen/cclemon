package org.cclemon.repository;

import org.cclemon.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Long> {

    List<Meal> findByUser_IdAndDateAndDeletedFalse(Long userId, String date);

    List<Meal> findByUser_IdAndDeletedFalse(Long userId);
}