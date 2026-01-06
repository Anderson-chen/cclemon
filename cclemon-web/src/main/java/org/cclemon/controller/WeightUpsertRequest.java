package org.cclemon.controller;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for receiving weight upsert requests from the API.
 * Validation is less strict than the internal command.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WeightUpsertRequest {

    // measureDate is optional in the request, will default to today in the controller.
    private LocalDate measureDate;

    @NotNull // weightKg is still required.
    @DecimalMin("20.0")
    @DecimalMax("300.0")
    private BigDecimal weightKg;

    private LocalTime measureTime;

    private String note;
}
