package org.cclemon.api;

import org.cclemon.api.vo.FoodItemResult;
import org.cclemon.api.vo.MealResult;
import org.cclemon.api.vo.command.AddFoodItemCommand;
import org.cclemon.api.vo.command.AddMealCommand;
import org.cclemon.api.vo.command.CopyYesterdayCommand;
import org.cclemon.api.vo.command.DeleteMealCommand;
import org.cclemon.api.vo.query.ListMealsQuery;

import java.util.List;

/**
 * 飲食管理 Handler 介面
 */
public interface DietHandler {

    List<MealResult> listMeals(ListMealsQuery query);

    MealResult addMeal(AddMealCommand command);

    void deleteMeal(DeleteMealCommand command);

    List<MealResult> copyYesterday(CopyYesterdayCommand command);

    List<FoodItemResult> listFoodLibrary(Long userId);

    FoodItemResult addFoodItem(AddFoodItemCommand command);
}
