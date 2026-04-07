package org.cclemon.controller;

import lombok.RequiredArgsConstructor;
import org.cclemon.api.DietHandler;
import org.cclemon.api.vo.FoodItemResult;
import org.cclemon.api.vo.command.AddFoodItemCommand;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/food-library")
@RequiredArgsConstructor
public class FoodLibraryController {

    private final DietHandler dietHandler;

    private Long getCurrentUserId() {
        return 1L;
    }

    @GetMapping
    public List<FoodItemResult> listFoodLibrary() {
        return dietHandler.listFoodLibrary(getCurrentUserId());
    }

    @PostMapping
    public FoodItemResult addFoodItem(@RequestBody AddFoodItemRequest request) {
        return dietHandler.addFoodItem(AddFoodItemCommand.builder()
                .name(request.name())
                .calories(request.calories())
                .protein(request.protein())
                .carbs(request.carbs())
                .fat(request.fat())
                .servingGrams(request.servingGrams())
                .build());
    }

    record AddFoodItemRequest(
            String name,
            Integer calories,
            Integer protein,
            Integer carbs,
            Integer fat,
            Integer servingGrams
    ) {}
}
