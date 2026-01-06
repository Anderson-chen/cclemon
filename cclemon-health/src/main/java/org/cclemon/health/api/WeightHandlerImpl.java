package org.cclemon.health.api;

import lombok.RequiredArgsConstructor;
import org.cclemon.api.WeightHandler;
import org.cclemon.api.vo.PagedResult;
import org.cclemon.api.vo.WeightChartData;
import org.cclemon.api.vo.WeightRecordResult;
import org.cclemon.api.vo.command.DeleteWeightCommand;
import org.cclemon.api.vo.command.RecordWeightCommand;
import org.cclemon.api.vo.query.GetWeightChartQuery;
import org.cclemon.api.vo.query.ListWeightsQuery;
import org.cclemon.entity.UserWeightLog;
import org.cclemon.health.internal.service.WeightService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WeightHandlerImpl implements WeightHandler {

    private final WeightService weightService;

    @Override
    @Transactional
    public WeightRecordResult record(RecordWeightCommand command) {
        UserWeightLog savedLog = weightService.upsertManualWeight(
                command.getUserId(),
                command.getMeasureDate(),
                command.getWeightKg(),
                command.getMeasureTime(),
                command.getNote()
        );
        // 這裡 savedLog.getUser() 應該已經被 populated
        return toWeightRecordResult(savedLog, savedLog.getUser().getId());
    }

    @Override
    @Transactional
    public void delete(DeleteWeightCommand command) {
        weightService.softDelete(command.getUserId(), command.getLogId());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<WeightRecordResult> list(ListWeightsQuery query) {
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());

        Page<UserWeightLog> logPage = weightService.listWeights(
                query.getUserId(),
                query.getStartDate(),
                query.getEndDate(),
                pageable
        );

        // 使用 query 中的 userId 來避免觸發 Lazy Load
        Page<WeightRecordResult> resultPage = logPage.map(log -> toWeightRecordResult(log, query.getUserId()));

        return PagedResult.<WeightRecordResult>builder()
                .content(resultPage.getContent())
                .page(resultPage.getNumber())
                .size(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .isLast(resultPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public WeightChartData getChart(GetWeightChartQuery query) {
        List<UserWeightLog> logs = weightService.listWeights(
                query.getUserId(),
                query.getStartDate(),
                query.getEndDate()
        );

        List<WeightChartData.DataPoint> dataPoints = logs.stream()
                .sorted(Comparator.comparing(UserWeightLog::getMeasureDate))
                .map(log -> WeightChartData.DataPoint.builder()
                        .date(log.getMeasureDate())
                        .weight(log.getWeightKg())
                        .build())
                .collect(Collectors.toList());

        if (dataPoints.isEmpty()) {
            return WeightChartData.builder().dataPoints(List.of()).build();
        }

        BigDecimal startWeight = dataPoints.get(0).getWeight();
        BigDecimal endWeight = dataPoints.get(dataPoints.size() - 1).getWeight();
        BigDecimal change = endWeight.subtract(startWeight);

        return WeightChartData.builder()
                .dataPoints(dataPoints)
                .startWeight(startWeight)
                .endWeight(endWeight)
                .change(change)
                .build();
    }

    private WeightRecordResult toWeightRecordResult(UserWeightLog log, Long userId) {
        return WeightRecordResult.builder()
                .logId(log.getId())
                .userId(userId)
                .measureDate(log.getMeasureDate())
                .weightKg(log.getWeightKg())
                .note(log.getNote())
                .build();
    }
}
