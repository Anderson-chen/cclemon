package org.cclemon.health.internal.service;

import lombok.RequiredArgsConstructor;
import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.UserGoalSetting;
import org.cclemon.repository.CclemonUserRepository;
import org.cclemon.repository.UserGoalSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class GoalsService {

    private final UserGoalSettingRepository userGoalSettingRepository;
    private final CclemonUserRepository cclemonUserRepository;

    @Transactional(readOnly = true)
    public UserGoalSetting getOrDefault(Long userId) {
        return userGoalSettingRepository.findByUser_Id(userId).orElseGet(UserGoalSetting::new);
    }

    @Transactional
    public UserGoalSetting updateGoals(Long userId, Integer dailyCalories, Integer weeklyExerciseDays,
                                       Integer dailyWaterMl, BigDecimal targetWeight) {
        CclemonUser user = cclemonUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        UserGoalSetting setting = userGoalSettingRepository.findByUser_Id(userId)
                .orElseGet(() -> {
                    UserGoalSetting s = new UserGoalSetting();
                    s.setUser(user);
                    return s;
                });

        if (dailyCalories != null) setting.setDailyCalories(dailyCalories);
        if (weeklyExerciseDays != null) setting.setWeeklyExerciseDays(weeklyExerciseDays);
        if (dailyWaterMl != null) setting.setDailyWaterMl(dailyWaterMl);
        if (targetWeight != null) setting.setTargetWeight(targetWeight);

        return userGoalSettingRepository.save(setting);
    }
}
