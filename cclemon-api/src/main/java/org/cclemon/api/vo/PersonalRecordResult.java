package org.cclemon.api.vo;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class PersonalRecordResult {
    String exerciseName;
    BigDecimal weight;
    Integer reps;
    String date;
}
