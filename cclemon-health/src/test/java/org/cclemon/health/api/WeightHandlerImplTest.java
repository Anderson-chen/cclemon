package org.cclemon.health.api;

import org.cclemon.api.vo.WeightChartData;
import org.cclemon.api.vo.WeightRecordResult;
import org.cclemon.api.vo.command.RecordWeightCommand;
import org.cclemon.api.vo.query.GetWeightChartQuery;
import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.UserWeightLog;
import org.cclemon.health.internal.service.WeightService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeightHandlerImplTest {

    @Mock
    private WeightService weightService;

    @InjectMocks
    private WeightHandlerImpl weightHandler;

    @Test
    void record_shouldCallServiceAndReturnResult() {
        // Arrange
        Long userId = 1L;
        RecordWeightCommand command = RecordWeightCommand.builder()
                .userId(userId)
                .measureDate(LocalDate.now())
                .weightKg(new BigDecimal("80.5"))
                .build();

        CclemonUser user = new CclemonUser();
        user.setId(userId);

        UserWeightLog savedLog = new UserWeightLog();
        savedLog.setId(100L);
        savedLog.setUser(user); // 使用 setUser 替代 setUserId
        savedLog.setMeasureDate(LocalDate.now());
        savedLog.setWeightKg(new BigDecimal("80.5"));

        when(weightService.upsertManualWeight(any(), any(), any(), any(), any())).thenReturn(savedLog);

        // Act
        WeightRecordResult result = weightHandler.record(command);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getLogId()).isEqualTo(100L);
        assertThat(result.getWeightKg()).isEqualByComparingTo("80.5");
        assertThat(result.getUserId()).isEqualTo(userId);
    }

    @Test
    void getChart_whenLogsExist_shouldReturnChartDataWithCorrectCalculations() {
        // Arrange
        GetWeightChartQuery query = GetWeightChartQuery.builder()
                .userId(1L)
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now())
                .build();

        UserWeightLog log1 = new UserWeightLog();
        log1.setMeasureDate(LocalDate.now().minusDays(1));
        log1.setWeightKg(new BigDecimal("75.0"));

        UserWeightLog log2 = new UserWeightLog();
        log2.setMeasureDate(LocalDate.now());
        log2.setWeightKg(new BigDecimal("76.5"));

        when(weightService.listWeights(any(), any(), any())).thenReturn(List.of(log1, log2));

        // Act
        WeightChartData result = weightHandler.getChart(query);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getDataPoints()).hasSize(2);
        assertThat(result.getStartWeight()).isEqualByComparingTo("75.0");
        assertThat(result.getEndWeight()).isEqualByComparingTo("76.5");
        assertThat(result.getChange()).isEqualByComparingTo("1.5");
    }

    @Test
    void getChart_whenNoLogs_shouldReturnEmptyChartData() {
        // Arrange
        GetWeightChartQuery query = GetWeightChartQuery.builder().userId(1L).startDate(LocalDate.now()).endDate(LocalDate.now()).build();
        when(weightService.listWeights(any(), any(), any())).thenReturn(List.of());

        // Act
        WeightChartData result = weightHandler.getChart(query);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getDataPoints()).isEmpty();
        assertThat(result.getStartWeight()).isNull();
        assertThat(result.getEndWeight()).isNull();
        assertThat(result.getChange()).isNull();
    }
}
