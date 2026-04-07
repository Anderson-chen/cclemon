package org.cclemon.api.vo.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateGoalsCommand {
    private Long userId;
    private Integer dailyCalories;
    private Integer weeklyExerciseDays;
    private Integer dailyWaterMl;
    private BigDecimal targetWeight;
}
