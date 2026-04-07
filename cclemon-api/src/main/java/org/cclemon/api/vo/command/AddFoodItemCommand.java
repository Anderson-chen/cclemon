package org.cclemon.api.vo.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AddFoodItemCommand {
    private String name;
    private Integer calories;
    private Integer protein;
    private Integer carbs;
    private Integer fat;
    private Integer servingGrams;
}
