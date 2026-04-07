package org.cclemon.health.internal.service;

import lombok.RequiredArgsConstructor;
import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.WaterDailyLog;
import org.cclemon.entity.WaterLogEntry;
import org.cclemon.repository.CclemonUserRepository;
import org.cclemon.repository.WaterDailyLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WaterService {

    private final WaterDailyLogRepository waterDailyLogRepository;
    private final CclemonUserRepository cclemonUserRepository;

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @Transactional(readOnly = true)
    public Optional<WaterDailyLog> getByDate(Long userId, String date) {
        return waterDailyLogRepository.findByUser_IdAndDate(userId, date);
    }

    @Transactional
    public WaterDailyLog logWater(Long userId, String date, Integer ml) {
        CclemonUser user = cclemonUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        WaterDailyLog dailyLog = waterDailyLogRepository.findByUser_IdAndDate(userId, date)
                .orElseGet(() -> {
                    WaterDailyLog newLog = new WaterDailyLog();
                    newLog.setUser(user);
                    newLog.setDate(date);
                    newLog.setTotalMl(0);
                    return newLog;
                });

        WaterLogEntry entry = new WaterLogEntry();
        entry.setDailyLog(dailyLog);
        entry.setLogTime(LocalTime.now().format(TIME_FORMAT));
        entry.setMl(ml);
        dailyLog.getLogs().add(entry);
        dailyLog.setTotalMl(dailyLog.getTotalMl() + ml);

        return waterDailyLogRepository.save(dailyLog);
    }
}
