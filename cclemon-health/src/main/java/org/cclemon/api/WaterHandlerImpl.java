package org.cclemon.api;

import lombok.RequiredArgsConstructor;
import org.cclemon.api.vo.WaterEntryResult;
import org.cclemon.api.vo.WaterLogEntryResult;
import org.cclemon.api.vo.command.LogWaterCommand;
import org.cclemon.api.vo.query.WaterQuery;
import org.cclemon.entity.WaterDailyLog;
import org.cclemon.service.WaterService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WaterHandlerImpl implements WaterHandler {

    private final WaterService waterService;

    @Override
    @Transactional(readOnly = true)
    public WaterEntryResult getToday(WaterQuery query) {
        Optional<WaterDailyLog> log = waterService.getByDate(query.getUserId(), query.getDate());
        return log.map(this::toResult).orElse(null);
    }

    @Override
    @Transactional
    public WaterEntryResult logWater(LogWaterCommand command) {
        WaterDailyLog log = waterService.logWater(command.getUserId(), command.getDate(), command.getMl());
        return toResult(log);
    }

    private WaterEntryResult toResult(WaterDailyLog log) {
        List<WaterLogEntryResult> entries = log.getLogs() == null ? List.of() :
                log.getLogs().stream()
                        .map(e -> WaterLogEntryResult.builder()
                                .time(e.getLogTime())
                                .ml(e.getMl())
                                .build())
                        .toList();

        return WaterEntryResult.builder()
                .id(String.valueOf(log.getId()))
                .userId(String.valueOf(log.getUser().getId()))
                .date(log.getDate())
                .totalMl(log.getTotalMl())
                .logs(entries)
                .build();
    }
}
