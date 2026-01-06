package org.cclemon.api.vo.command;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DeleteWeightCommand {

    @NotNull
    Long userId;

    @NotNull
    Long logId;
}
