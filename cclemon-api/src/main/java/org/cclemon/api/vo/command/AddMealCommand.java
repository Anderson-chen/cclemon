package org.cclemon.api.vo.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AddMealCommand {
    private Long userId;
    private String date;
    private String type;
    private List<FoodEntryCommand> foods;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class FoodEntryCommand {
        private String foodId;
        private String name;
        private Integer calories;
        private Integer protein;
        private Integer carbs;
        private Integer fat;
        private Integer amount;
    }
}
