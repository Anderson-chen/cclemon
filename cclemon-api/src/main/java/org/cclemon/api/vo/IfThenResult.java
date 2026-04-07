package org.cclemon.api.vo;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class IfThenResult {
    String id;
    String trigger;
    String action;
    String category;
    String atRiskWindowStart;
    String atRiskWindowEnd;
    Boolean active;
}
