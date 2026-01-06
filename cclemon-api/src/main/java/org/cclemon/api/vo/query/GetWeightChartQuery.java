package org.cclemon.api.vo.query;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GetWeightChartQuery {

    @NotNull
    Long userId;

    @NotNull
    LocalDate startDate;

    @NotNull
    LocalDate endDate;
}
