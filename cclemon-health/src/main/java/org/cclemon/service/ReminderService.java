package org.cclemon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.Reminder;
import org.cclemon.repository.ReminderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderService {

    private final ReminderRepository reminderRepository;

    /**
     * 獲取用戶的所有提醒
     */
    public Page<Reminder> getUserReminders(CclemonUser user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return reminderRepository.findByUserAndDeletedFalse(user, pageable);
    }

    /**
     * 獲取用戶的活躍提醒
     */
    public List<Reminder> getActiveReminders(CclemonUser user) {
        return reminderRepository.findByUserAndIsActiveAndDeletedFalse(user, true);
    }

    /**
     * 根據類型獲取提醒
     */
    public List<Reminder> getRemindersByType(CclemonUser user, String reminderType) {
        return reminderRepository.findByUserAndReminderTypeAndDeletedFalse(user, reminderType);
    }

    /**
     * 獲取今日需要觸發的提醒
     */
    public List<Reminder> getTodayReminders(CclemonUser user) {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        return reminderRepository.findActiveRemindersByUserAndDate(user, today);
    }

    /**
     * 創建新提醒
     */
    @Transactional
    public Reminder createReminder(CclemonUser user, Reminder reminder) {
        reminder.setUser(user);
        reminder.setDeleted(false);
        
        // 計算下一次觸發時間
        updateNextTrigger(reminder);
        
        log.info("創建提醒: 用戶={}, 類型={}, 標題={}", user.getId(), reminder.getReminderType(), reminder.getTitle());
        return reminderRepository.save(reminder);
    }

    /**
     * 更新提醒
     */
    @Transactional
    public Reminder updateReminder(Long reminderId, Reminder updatedReminder) {
        Optional<Reminder> existingReminder = reminderRepository.findById(reminderId);
        if (existingReminder.isEmpty() || existingReminder.get().getDeleted()) {
            throw new RuntimeException("提醒不存在: " + reminderId);
        }

        Reminder reminder = existingReminder.get();
        reminder.setTitle(updatedReminder.getTitle());
        reminder.setDescription(updatedReminder.getDescription());
        reminder.setReminderType(updatedReminder.getReminderType());
        reminder.setReminderTime(updatedReminder.getReminderTime());
        reminder.setRepeatPattern(updatedReminder.getRepeatPattern());
        reminder.setRepeatDays(updatedReminder.getRepeatDays());
        reminder.setIsActive(updatedReminder.getIsActive());

        // 重新計算下一次觸發時間
        updateNextTrigger(reminder);

        log.info("更新提醒: ID={}, 標題={}", reminderId, reminder.getTitle());
        return reminderRepository.save(reminder);
    }

    /**
     * 刪除提醒（軟刪除）
     */
    @Transactional
    public void deleteReminder(Long reminderId) {
        Optional<Reminder> reminder = reminderRepository.findById(reminderId);
        if (reminder.isPresent() && !reminder.get().getDeleted()) {
            reminder.get().setDeleted(true);
            reminder.get().setIsActive(false);
            reminderRepository.save(reminder.get());
            log.info("刪除提醒: ID={}", reminderId);
        }
    }

    /**
     * 啟用/停用提醒
     */
    @Transactional
    public Reminder toggleReminderActive(Long reminderId, boolean isActive) {
        Optional<Reminder> reminder = reminderRepository.findById(reminderId);
        if (reminder.isEmpty() || reminder.get().getDeleted()) {
            throw new RuntimeException("提醒不存在: " + reminderId);
        }

        reminder.get().setIsActive(isActive);
        if (isActive) {
            updateNextTrigger(reminder.get());
        }

        log.info("切換提醒狀態: ID={}, 活躍={}", reminderId, isActive);
        return reminderRepository.save(reminder.get());
    }

    /**
     * 觸發提醒
     */
    @Transactional
    public void triggerReminder(Long reminderId) {
        Optional<Reminder> reminder = reminderRepository.findById(reminderId);
        if (reminder.isEmpty() || reminder.get().getDeleted() || !reminder.get().getIsActive()) {
            return;
        }

        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        reminder.get().setLastTriggered(today);
        
        // 計算下一次觸發時間
        updateNextTrigger(reminder.get());
        
        reminderRepository.save(reminder.get());
        log.info("觸發提醒: ID={}, 日期={}", reminderId, today);
    }

    /**
     * 獲取用戶活躍提醒數量
     */
    public long getActiveReminderCount(CclemonUser user) {
        return reminderRepository.countByUserAndIsActiveAndDeletedFalse(user, true);
    }

    /**
     * 計算下一次觸發時間
     */
    private void updateNextTrigger(Reminder reminder) {
        LocalDate nextTrigger = LocalDate.now();
        
        switch (reminder.getRepeatPattern()) {
            case "daily":
                nextTrigger = nextTrigger.plusDays(1);
                break;
            case "weekly":
                nextTrigger = nextTrigger.plusWeeks(1);
                break;
            case "monthly":
                nextTrigger = nextTrigger.plusMonths(1);
                break;
            case "custom":
                // 這裡可以根據 repeatDays 實現自定義邏輯
                nextTrigger = nextTrigger.plusDays(1);
                break;
            default:
                nextTrigger = nextTrigger.plusDays(1);
        }
        
        reminder.setNextTrigger(nextTrigger.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }
}