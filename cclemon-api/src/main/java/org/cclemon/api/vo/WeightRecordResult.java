package org.cclemon.api.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Value;

/**
 * Result DTO for a weight record.
 */
@Value
@Builder
public class WeightRecordResult {

    Long logId;
    Long userId;
    LocalDate measureDate;
    BigDecimal weightKg;
    BigDecimal bmi;
    String note;
}
