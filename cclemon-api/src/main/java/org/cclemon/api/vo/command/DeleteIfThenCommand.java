package org.cclemon.api.vo.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DeleteIfThenCommand {
    private Long userId;
    private Long planId;
}
