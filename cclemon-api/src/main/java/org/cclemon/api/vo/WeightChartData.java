package org.cclemon.api.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class WeightChartData {

    List<DataPoint> dataPoints;
    BigDecimal startWeight;
    BigDecimal endWeight;
    BigDecimal change;

    @Value
    @Builder
    public static class DataPoint {
        LocalDate date;
        BigDecimal weight;
    }
}
