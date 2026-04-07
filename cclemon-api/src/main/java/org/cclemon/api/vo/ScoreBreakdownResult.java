package org.cclemon.api.vo;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ScoreBreakdownResult {
    Integer diet;
    Integer exercise;
    Integer water;
    Integer consistency;
}
