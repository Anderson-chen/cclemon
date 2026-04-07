package org.cclemon.health.internal.service;

import lombok.RequiredArgsConstructor;
import org.cclemon.api.vo.ScoreBreakdownResult;
import org.cclemon.api.vo.ScoreResult;
import org.cclemon.repository.MealRepository;
import org.cclemon.repository.UserGoalSettingRepository;
import org.cclemon.repository.UserWeightLogRepository;
import org.cclemon.repository.WaterDailyLogRepository;
import org.cclemon.repository.WorkoutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScoreService {

    private final MealRepository mealRepository;
    private final WorkoutRepository workoutRepository;
    private final WaterDailyLogRepository waterDailyLogRepository;
    private final UserWeightLogRepository userWeightLogRepository;
    private final UserGoalSettingRepository userGoalSettingRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    @Transactional(readOnly = true)
    public ScoreResult scoreForDate(Long userId, LocalDate date) {
        String dateStr = date.format(DATE_FMT);

        int diet = mealRepository.findByUser_IdAndDateAndDeletedFalse(userId, dateStr).isEmpty() ? 0 : 25;
        int exercise = workoutRepository.findByUser_IdAndDateAndDeletedFalse(userId, dateStr).isEmpty() ? 0 : 25;

        int targetWater = userGoalSettingRepository.findByUser_Id(userId)
                .map(s -> s.getDailyWaterMl() != null ? s.getDailyWaterMl() : 2000)
                .orElse(2000);
        int actualWater = waterDailyLogRepository.findByUser_IdAndDate(userId, dateStr)
                .map(w -> w.getTotalMl() != null ? w.getTotalMl() : 0)
                .orElse(0);
        int water = actualWater >= targetWater ? 25 : 0;

        boolean hasWeight = userWeightLogRepository.existsByUser_IdAndMeasureDateAndDeletedFalse(userId, date);
        int consistency = hasWeight ? 25 : 0;

        int total = diet + exercise + water + consistency;

        return ScoreResult.builder()
                .date(dateStr)
                .score(total)
                .breakdown(ScoreBreakdownResult.builder()
                        .diet(diet)
                        .exercise(exercise)
                        .water(water)
                        .consistency(consistency)
                        .build())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ScoreResult> scoreForRange(Long userId, LocalDate from, LocalDate to) {
        List<ScoreResult> results = new ArrayList<>();
        LocalDate current = from;
        while (!current.isAfter(to)) {
            results.add(scoreForDate(userId, current));
            current = current.plusDays(1);
        }
        return results;
    }
}
