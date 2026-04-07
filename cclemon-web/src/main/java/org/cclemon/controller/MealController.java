package org.cclemon.controller;

import lombok.RequiredArgsConstructor;
import org.cclemon.api.DietHandler;
import org.cclemon.api.vo.MealResult;
import org.cclemon.api.vo.command.AddMealCommand;
import org.cclemon.api.vo.command.CopyYesterdayCommand;
import org.cclemon.api.vo.command.DeleteMealCommand;
import org.cclemon.api.vo.query.ListMealsQuery;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/meals")
@RequiredArgsConstructor
public class MealController {

    private final DietHandler dietHandler;

    private Long getCurrentUserId() {
        return 1L;
    }

    @GetMapping
    public List<MealResult> listMeals(@RequestParam(value = "date", required = false) String date) {
        String effectiveDate = date != null ? date : LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        return dietHandler.listMeals(ListMealsQuery.builder()
                .userId(getCurrentUserId())
                .date(effectiveDate)
                .build());
    }

    @PostMapping
    public MealResult addMeal(@RequestBody AddMealRequest request) {
        String date = request.date() != null ? request.date() : LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        List<AddMealCommand.FoodEntryCommand> foods = request.foods() == null ? List.of() :
                request.foods().stream()
                        .map(f -> AddMealCommand.FoodEntryCommand.builder()
                                .foodId(f.foodId())
                                .name(f.name())
                                .calories(f.calories())
                                .protein(f.protein())
                                .carbs(f.carbs())
                                .fat(f.fat())
                                .amount(f.amount())
                                .build())
                        .toList();

        return dietHandler.addMeal(AddMealCommand.builder()
                .userId(getCurrentUserId())
                .date(date)
                .type(request.type())
                .foods(foods)
                .build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMeal(@PathVariable("id") Long id) {
        dietHandler.deleteMeal(DeleteMealCommand.builder()
                .userId(getCurrentUserId())
                .mealId(id)
                .build());
    }

    @PostMapping("/copy-yesterday")
    public List<MealResult> copyYesterday() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        return dietHandler.copyYesterday(CopyYesterdayCommand.builder()
                .userId(getCurrentUserId())
                .today(today.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .yesterday(yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .build());
    }

    record AddMealRequest(
            String date,
            String type,
            List<FoodEntryRequest> foods
    ) {}

    record FoodEntryRequest(
            String foodId,
            String name,
            Integer calories,
            Integer protein,
            Integer carbs,
            Integer fat,
            Integer amount
    ) {}
}
