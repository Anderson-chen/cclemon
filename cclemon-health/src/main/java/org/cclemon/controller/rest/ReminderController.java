package org.cclemon.controller.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.Reminder;
import org.cclemon.service.CclemonUserService;
import org.cclemon.service.ReminderService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ReminderController {

    private final ReminderService reminderService;
    private final CclemonUserService userService;

    /**
     * 獲取用戶的所有提醒
     */
    @GetMapping
    public ResponseEntity<Page<Reminder>> getUserReminders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            Page<Reminder> reminders = reminderService.getUserReminders(currentUser, page, size);
            return ResponseEntity.ok(reminders);
        } catch (Exception e) {
            log.error("獲取用戶提醒失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 獲取活躍的提醒
     */
    @GetMapping("/active")
    public ResponseEntity<List<Reminder>> getActiveReminders() {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            List<Reminder> reminders = reminderService.getActiveReminders(currentUser);
            return ResponseEntity.ok(reminders);
        } catch (Exception e) {
            log.error("獲取活躍提醒失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根據類型獲取提醒
     */
    @GetMapping("/type/{reminderType}")
    public ResponseEntity<List<Reminder>> getRemindersByType(@PathVariable String reminderType) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            List<Reminder> reminders = reminderService.getRemindersByType(currentUser, reminderType);
            return ResponseEntity.ok(reminders);
        } catch (Exception e) {
            log.error("根據類型獲取提醒失敗: {}", reminderType, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 獲取今日提醒
     */
    @GetMapping("/today")
    public ResponseEntity<List<Reminder>> getTodayReminders() {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            List<Reminder> reminders = reminderService.getTodayReminders(currentUser);
            return ResponseEntity.ok(reminders);
        } catch (Exception e) {
            log.error("獲取今日提醒失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 創建新提醒
     */
    @PostMapping
    public ResponseEntity<Reminder> createReminder(@RequestBody Reminder reminder) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            Reminder createdReminder = reminderService.createReminder(currentUser, reminder);
            return ResponseEntity.ok(createdReminder);
        } catch (Exception e) {
            log.error("創建提醒失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 更新提醒
     */
    @PutMapping("/{id}")
    public ResponseEntity<Reminder> updateReminder(@PathVariable Long id, @RequestBody Reminder reminder) {
        try {
            Reminder updatedReminder = reminderService.updateReminder(id, reminder);
            return ResponseEntity.ok(updatedReminder);
        } catch (RuntimeException e) {
            log.error("更新提醒失敗: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("更新提醒失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 刪除提醒
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long id) {
        try {
            reminderService.deleteReminder(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("刪除提醒失敗: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 切換提醒狀態
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Reminder> toggleReminderActive(@PathVariable Long id, @RequestParam boolean isActive) {
        try {
            Reminder reminder = reminderService.toggleReminderActive(id, isActive);
            return ResponseEntity.ok(reminder);
        } catch (RuntimeException e) {
            log.error("切換提醒狀態失敗: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("切換提醒狀態失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 觸發提醒
     */
    @PostMapping("/{id}/trigger")
    public ResponseEntity<Void> triggerReminder(@PathVariable Long id) {
        try {
            reminderService.triggerReminder(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("觸發提醒失敗: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 獲取活躍提醒數量
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getActiveReminderCount() {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            long count = reminderService.getActiveReminderCount(currentUser);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("獲取活躍提醒數量失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}