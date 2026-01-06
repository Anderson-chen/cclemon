package org.cclemon.api.vo.command;


import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Command for recording a user's weight.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RecordWeightCommand {

    @NotNull
    Long userId;

    @NotNull
    LocalDate measureDate;

    @NotNull
    @DecimalMin("20.0")
    @DecimalMax("300.0")
    BigDecimal weightKg;

    LocalTime measureTime;

    String note;
}
