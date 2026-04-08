package org.cclemon.service;

import lombok.RequiredArgsConstructor;
import org.cclemon.entity.Food;
import org.cclemon.repository.FoodRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodLibraryService {

    private final FoodRepository foodRepository;

    @Transactional(readOnly = true)
    public List<Food> listAll() {
        return foodRepository.findByDeletedFalse();
    }

    @Transactional
    public Food addFood(String name, Integer calories, Integer protein, Integer carbs, Integer fat, Integer servingGrams) {
        Food food = new Food();
        food.setName(name);
        food.setCalorie(calories == null ? null : calories.longValue());
        food.setProtein(protein == null ? null : protein.longValue());
        food.setCarbs(carbs == null ? null : carbs.longValue());
        food.setFat(fat == null ? null : fat.longValue());
        return foodRepository.save(food);
    }
}
