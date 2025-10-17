package org.cclemon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.UserPreference;
import org.cclemon.repository.UserPreferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPreferenceService {

    private final UserPreferenceRepository userPreferenceRepository;

    /**
     * 獲取用戶偏好設定
     */
    public UserPreference getUserPreference(CclemonUser user) {
        Optional<UserPreference> preference = userPreferenceRepository.findByUserAndDeletedFalse(user);
        return preference.orElseGet(() -> createDefaultPreference(user));
    }

    /**
     * 更新用戶偏好設定
     */
    @Transactional
    public UserPreference updateUserPreference(CclemonUser user, UserPreference updatedPreference) {
        Optional<UserPreference> existingPreference = userPreferenceRepository.findByUser(user);
        
        UserPreference preference;
        if (existingPreference.isPresent()) {
            preference = existingPreference.get();
            updatePreferenceFields(preference, updatedPreference);
        } else {
            preference = updatedPreference;
            preference.setUser(user);
            preference.setDeleted(false);
        }

        log.info("更新用戶偏好設定: 用戶={}", user.getId());
        return userPreferenceRepository.save(preference);
    }

    /**
     * 創建默認偏好設定
     */
    @Transactional
    public UserPreference createDefaultPreference(CclemonUser user) {
        UserPreference preference = new UserPreference();
        preference.setUser(user);
        preference.setDeleted(false);
        
        log.info("創建默認偏好設定: 用戶={}", user.getId());
        return userPreferenceRepository.save(preference);
    }

    /**
     * 更新偏好設定字段
     */
    private void updatePreferenceFields(UserPreference existing, UserPreference updated) {
        if (updated.getLanguage() != null) existing.setLanguage(updated.getLanguage());
        if (updated.getTheme() != null) existing.setTheme(updated.getTheme());
        if (updated.getDateFormat() != null) existing.setDateFormat(updated.getDateFormat());
        if (updated.getTimeFormat() != null) existing.setTimeFormat(updated.getTimeFormat());
        
        if (updated.getWeightUnit() != null) existing.setWeightUnit(updated.getWeightUnit());
        if (updated.getHeightUnit() != null) existing.setHeightUnit(updated.getHeightUnit());
        if (updated.getTemperatureUnit() != null) existing.setTemperatureUnit(updated.getTemperatureUnit());
        if (updated.getDistanceUnit() != null) existing.setDistanceUnit(updated.getDistanceUnit());
        
        if (updated.getEmailNotifications() != null) existing.setEmailNotifications(updated.getEmailNotifications());
        if (updated.getPushNotifications() != null) existing.setPushNotifications(updated.getPushNotifications());
        if (updated.getReminderSound() != null) existing.setReminderSound(updated.getReminderSound());
        if (updated.getVibration() != null) existing.setVibration(updated.getVibration());
        
        if (updated.getDataSharing() != null) existing.setDataSharing(updated.getDataSharing());
        if (updated.getAnalyticsTracking() != null) existing.setAnalyticsTracking(updated.getAnalyticsTracking());
        if (updated.getProfileVisibility() != null) existing.setProfileVisibility(updated.getProfileVisibility());
        
        if (updated.getAutoSync() != null) existing.setAutoSync(updated.getAutoSync());
        if (updated.getBackupEnabled() != null) existing.setBackupEnabled(updated.getBackupEnabled());
        if (updated.getGoalReminders() != null) existing.setGoalReminders(updated.getGoalReminders());
        if (updated.getWeeklyReports() != null) existing.setWeeklyReports(updated.getWeeklyReports());
        if (updated.getMonthlyReports() != null) existing.setMonthlyReports(updated.getMonthlyReports());
        
        if (updated.getCustomSettings() != null) existing.setCustomSettings(updated.getCustomSettings());
    }
}