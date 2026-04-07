package org.cclemon.health.api;

import lombok.RequiredArgsConstructor;
import org.cclemon.api.DietHandler;
import org.cclemon.api.vo.FoodEntryResult;
import org.cclemon.api.vo.FoodItemResult;
import org.cclemon.api.vo.MealResult;
import org.cclemon.api.vo.command.AddFoodItemCommand;
import org.cclemon.api.vo.command.AddMealCommand;
import org.cclemon.api.vo.command.CopyYesterdayCommand;
import org.cclemon.api.vo.command.DeleteMealCommand;
import org.cclemon.api.vo.query.ListMealsQuery;
import org.cclemon.entity.Food;
import org.cclemon.entity.Meal;
import org.cclemon.entity.MealFoodEntry;
import org.cclemon.health.internal.service.FoodLibraryService;
import org.cclemon.health.internal.service.MealService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DietHandlerImpl implements DietHandler {

    private final MealService mealService;
    private final FoodLibraryService foodLibraryService;

    @Override
    @Transactional(readOnly = true)
    public List<MealResult> listMeals(ListMealsQuery query) {
        return mealService.listByDate(query.getUserId(), query.getDate())
                .stream().map(this::toMealResult).toList();
    }

    @Override
    @Transactional
    public MealResult addMeal(AddMealCommand command) {
        List<MealFoodEntry> foods = new ArrayList<>();
        if (command.getFoods() != null) {
            for (AddMealCommand.FoodEntryCommand f : command.getFoods()) {
                MealFoodEntry entry = new MealFoodEntry();
                entry.setFoodId(f.getFoodId());
                entry.setName(f.getName());
                entry.setCalories(f.getCalories());
                entry.setProtein(f.getProtein());
                entry.setCarbs(f.getCarbs());
                entry.setFat(f.getFat());
                entry.setAmount(f.getAmount());
                foods.add(entry);
            }
        }
        Meal meal = mealService.addMeal(command.getUserId(), command.getDate(), command.getType(), foods);
        return toMealResult(meal);
    }

    @Override
    @Transactional
    public void deleteMeal(DeleteMealCommand command) {
        mealService.softDelete(command.getUserId(), command.getMealId());
    }

    @Override
    @Transactional
    public List<MealResult> copyYesterday(CopyYesterdayCommand command) {
        return mealService.copyYesterday(command.getUserId(), command.getYesterday(), command.getToday())
                .stream().map(this::toMealResult).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodItemResult> listFoodLibrary(Long userId) {
        return foodLibraryService.listAll().stream().map(this::toFoodItemResult).toList();
    }

    @Override
    @Transactional
    public FoodItemResult addFoodItem(AddFoodItemCommand command) {
        Food food = foodLibraryService.addFood(
                command.getName(),
                command.getCalories(),
                command.getProtein(),
                command.getCarbs(),
                command.getFat(),
                command.getServingGrams()
        );
        return toFoodItemResult(food);
    }

    private MealResult toMealResult(Meal meal) {
        List<FoodEntryResult> foods = meal.getFoods() == null ? List.of() :
                meal.getFoods().stream()
                        .filter(f -> f.getDeleted() == null || !f.getDeleted())
                        .map(f -> FoodEntryResult.builder()
                                .foodId(f.getFoodId())
                                .name(f.getName())
                                .calories(f.getCalories())
                                .protein(f.getProtein())
                                .carbs(f.getCarbs())
                                .fat(f.getFat())
                                .amount(f.getAmount())
                                .build())
                        .toList();

        return MealResult.builder()
                .id(String.valueOf(meal.getId()))
                .userId(String.valueOf(meal.getUser().getId()))
                .date(meal.getDate())
                .type(meal.getType())
                .foods(foods)
                .createdAt(meal.getCreateTime() != null ? meal.getCreateTime().toString() : null)
                .build();
    }

    private FoodItemResult toFoodItemResult(Food food) {
        return FoodItemResult.builder()
                .id(String.valueOf(food.getId()))
                .name(food.getName())
                .calories(food.getCalorie() != null ? food.getCalorie().intValue() : null)
                .protein(food.getProtein() != null ? food.getProtein().intValue() : null)
                .carbs(food.getCarbs() != null ? food.getCarbs().intValue() : null)
                .fat(food.getFat() != null ? food.getFat().intValue() : null)
                .servingGrams(null)
                .build();
    }
}
