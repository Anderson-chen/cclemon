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
 * Supports both UI format (date/weight) and legacy format (measureDate/weightKg).
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WeightUpsertRequest {

    // UI format: date (maps to measureDate)
    private LocalDate date;

    // Legacy format: measureDate
    private LocalDate measureDate;

    // UI format: weight (maps to weightKg)
    @DecimalMin("20.0")
    @DecimalMax("300.0")
    private BigDecimal weight;

    // Legacy format: weightKg
    @DecimalMin("20.0")
    @DecimalMax("300.0")
    private BigDecimal weightKg;

    private LocalTime measureTime;

    private String note;

    /**
     * Returns the effective date, preferring UI format field.
     */
    public LocalDate getDate() {
        return date != null ? date : measureDate;
    }

    /**
     * Returns the effective weight, preferring UI format field.
     */
    @NotNull
    public BigDecimal getWeight() {
        return weight != null ? weight : weightKg;
    }
}