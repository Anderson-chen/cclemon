package org.cclemon.api.vo.query;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ListWeightsQuery {

    @NotNull
    Long userId;

    @NotNull
    LocalDate startDate;

    @NotNull
    LocalDate endDate;

    @Min(0)
    int page;

    @Min(1)
    int size;
}
