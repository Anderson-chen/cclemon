package org.cclemon.api.vo.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateIfThenCommand {
    private Long userId;
    private Long planId;
    private String trigger;
    private String action;
    private String category;
    private String atRiskWindowStart;
    private String atRiskWindowEnd;
    private Boolean active;
}
