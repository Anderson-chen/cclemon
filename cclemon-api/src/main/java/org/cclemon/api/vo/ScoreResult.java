package org.cclemon.api.vo;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ScoreResult {
    String date;
    Integer score;
    ScoreBreakdownResult breakdown;
}
