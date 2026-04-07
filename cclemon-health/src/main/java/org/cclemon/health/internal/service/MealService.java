package org.cclemon.health.internal.service;

import lombok.RequiredArgsConstructor;
import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.Meal;
import org.cclemon.entity.MealFoodEntry;
import org.cclemon.repository.CclemonUserRepository;
import org.cclemon.repository.MealRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealRepository mealRepository;
    private final CclemonUserRepository cclemonUserRepository;

    @Transactional(readOnly = true)
    public List<Meal> listByDate(Long userId, String date) {
        return mealRepository.findByUser_IdAndDateAndDeletedFalse(userId, date);
    }

    @Transactional
    public Meal addMeal(Long userId, String date, String type, List<MealFoodEntry> foods) {
        CclemonUser user = cclemonUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        Meal meal = new Meal();
        meal.setUser(user);
        meal.setDate(date);
        meal.setType(type);

        for (MealFoodEntry food : foods) {
            food.setMeal(meal);
        }
        meal.setFoods(foods);

        return mealRepository.save(meal);
    }

    @Transactional
    public void softDelete(Long userId, Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                .filter(m -> m.getUser().getId().equals(userId))
                .orElseThrow(() -> new NoSuchElementException("Meal not found: " + mealId));
        meal.setDeleted(true);
        mealRepository.save(meal);
    }

    @Transactional
    public List<Meal> copyYesterday(Long userId, String yesterday, String today) {
        List<Meal> yesterdayMeals = mealRepository.findByUser_IdAndDateAndDeletedFalse(userId, yesterday);
        CclemonUser user = cclemonUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        List<Meal> copiedMeals = new ArrayList<>();
        for (Meal source : yesterdayMeals) {
            Meal copy = new Meal();
            copy.setUser(user);
            copy.setDate(today);
            copy.setType(source.getType());

            List<MealFoodEntry> copiedFoods = new ArrayList<>();
            for (MealFoodEntry sourceFoodEntry : source.getFoods()) {
                MealFoodEntry entry = new MealFoodEntry();
                entry.setMeal(copy);
                entry.setFoodId(sourceFoodEntry.getFoodId());
                entry.setName(sourceFoodEntry.getName());
                entry.setCalories(sourceFoodEntry.getCalories());
                entry.setProtein(sourceFoodEntry.getProtein());
                entry.setCarbs(sourceFoodEntry.getCarbs());
                entry.setFat(sourceFoodEntry.getFat());
                entry.setAmount(sourceFoodEntry.getAmount());
                copiedFoods.add(entry);
            }
            copy.setFoods(copiedFoods);
            copiedMeals.add(mealRepository.save(copy));
        }
        return copiedMeals;
    }
}
