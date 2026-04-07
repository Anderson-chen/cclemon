package org.cclemon.api.vo;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ExerciseEntryResult {
    String name;
    Integer sets;
    Integer reps;
    BigDecimal weight;
    Integer durationMinutes;
    Boolean isPR;
}
