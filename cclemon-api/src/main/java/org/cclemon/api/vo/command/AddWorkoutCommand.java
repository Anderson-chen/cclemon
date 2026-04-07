package org.cclemon.api.vo.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AddWorkoutCommand {
    private Long userId;
    private String date;
    private List<ExerciseEntryCommand> exercises;
    private String notes;
    private Integer totalMinutes;
    private Integer estimatedCaloriesBurned;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class ExerciseEntryCommand {
        private String name;
        private Integer sets;
        private Integer reps;
        private BigDecimal weight;
        private Integer durationMinutes;
        private Boolean isPR;
    }
}
