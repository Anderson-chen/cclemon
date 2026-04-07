package org.cclemon.api.vo;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class MealResult {
    String id;
    String userId;
    String date;
    String type;
    List<FoodEntryResult> foods;
    String createdAt;
}
