package org.cclemon.controller.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.UserPreference;
import org.cclemon.service.CclemonUserService;
import org.cclemon.service.UserPreferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-preferences")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserPreferenceController {

    private final UserPreferenceService userPreferenceService;
    private final CclemonUserService userService;

    /**
     * 獲取用戶偏好設定
     */
    @GetMapping
    public ResponseEntity<UserPreference> getUserPreference() {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            UserPreference preference = userPreferenceService.getUserPreference(currentUser);
            return ResponseEntity.ok(preference);
        } catch (Exception e) {
            log.error("獲取用戶偏好設定失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 更新用戶偏好設定
     */
    @PutMapping
    public ResponseEntity<UserPreference> updateUserPreference(@RequestBody UserPreference preference) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            UserPreference updatedPreference = userPreferenceService.updateUserPreference(currentUser, preference);
            return ResponseEntity.ok(updatedPreference);
        } catch (Exception e) {
            log.error("更新用戶偏好設定失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 重置為默認設定
     */
    @PostMapping("/reset")
    public ResponseEntity<UserPreference> resetToDefault() {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            UserPreference defaultPreference = userPreferenceService.createDefaultPreference(currentUser);
            return ResponseEntity.ok(defaultPreference);
        } catch (Exception e) {
            log.error("重置用戶偏好設定失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}