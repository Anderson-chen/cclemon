package org.cclemon.api.vo;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FoodEntryResult {
    String foodId;
    String name;
    Integer calories;
    Integer protein;
    Integer carbs;
    Integer fat;
    Integer amount;
}
