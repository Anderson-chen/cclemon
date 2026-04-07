package org.cclemon.api.vo;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class WorkoutResult {
    String id;
    String userId;
    String date;
    List<ExerciseEntryResult> exercises;
    String notes;
    Integer totalMinutes;
    Integer estimatedCaloriesBurned;
}
