package org.cclemon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.HealthGoal;
import org.cclemon.repository.HealthGoalRepository;
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
public class HealthGoalService {

    private final HealthGoalRepository healthGoalRepository;

    /**
     * 獲取用戶的所有健康目標
     */
    public Page<HealthGoal> getUserHealthGoals(CclemonUser user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return healthGoalRepository.findByUserAndDeletedFalseOrderByCreateTimeDesc(user, pageable);
    }

    /**
     * 根據狀態獲取健康目標
     */
    public List<HealthGoal> getGoalsByStatus(CclemonUser user, String status) {
        return healthGoalRepository.findByUserAndGoalStatusAndDeletedFalse(user, status);
    }

    /**
     * 根據類型獲取健康目標
     */
    public List<HealthGoal> getGoalsByType(CclemonUser user, String goalType) {
        return healthGoalRepository.findByUserAndGoalTypeAndDeletedFalse(user, goalType);
    }

    /**
     * 獲取活躍的健康目標（按優先級和日期排序）
     */
    public List<HealthGoal> getActiveGoals(CclemonUser user) {
        return healthGoalRepository.findActiveGoalsByUserOrderByPriorityAndDate(user);
    }

    /**
     * 獲取指定日期到期的目標
     */
    public List<HealthGoal> getGoalsByTargetDate(CclemonUser user, String targetDate) {
        return healthGoalRepository.findActiveGoalsByUserAndTargetDate(user, targetDate);
    }

    /**
     * 創建新健康目標
     */
    @Transactional
    public HealthGoal createHealthGoal(CclemonUser user, HealthGoal goal) {
        goal.setUser(user);
        goal.setDeleted(false);
        goal.setStartDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        
        if (goal.getGoalStatus() == null) {
            goal.setGoalStatus("active");
        }
        
        if (goal.getCurrentValue() == null) {
            goal.setCurrentValue(0.0);
        }
        
        if (goal.getProgressPercentage() == null) {
            goal.setProgressPercentage(0.0);
        }

        // 計算初始進度
        updateProgressPercentage(goal);

        log.info("創建健康目標: 用戶={}, 類型={}, 標題={}", user.getId(), goal.getGoalType(), goal.getTitle());
        return healthGoalRepository.save(goal);
    }

    /**
     * 更新健康目標
     */
    @Transactional
    public HealthGoal updateHealthGoal(Long goalId, HealthGoal updatedGoal) {
        Optional<HealthGoal> existingGoal = healthGoalRepository.findById(goalId);
        if (existingGoal.isEmpty() || existingGoal.get().getDeleted()) {
            throw new RuntimeException("健康目標不存在: " + goalId);
        }

        HealthGoal goal = existingGoal.get();
        goal.setTitle(updatedGoal.getTitle());
        goal.setDescription(updatedGoal.getDescription());
        goal.setGoalType(updatedGoal.getGoalType());
        goal.setTargetValue(updatedGoal.getTargetValue());
        goal.setCurrentValue(updatedGoal.getCurrentValue());
        goal.setTargetDate(updatedGoal.getTargetDate());
        goal.setGoalStatus(updatedGoal.getGoalStatus());
        goal.setUnit(updatedGoal.getUnit());
        goal.setPriorityLevel(updatedGoal.getPriorityLevel());
        goal.setNotes(updatedGoal.getNotes());
        goal.setReminderEnabled(updatedGoal.getReminderEnabled());

        // 更新進度百分比
        updateProgressPercentage(goal);

        // 檢查是否達成目標
        checkGoalAchievement(goal);

        log.info("更新健康目標: ID={}, 標題={}", goalId, goal.getTitle());
        return healthGoalRepository.save(goal);
    }

    /**
     * 更新目標進度
     */
    @Transactional
    public HealthGoal updateGoalProgress(Long goalId, Double currentValue) {
        Optional<HealthGoal> goal = healthGoalRepository.findById(goalId);
        if (goal.isEmpty() || goal.get().getDeleted()) {
            throw new RuntimeException("健康目標不存在: " + goalId);
        }

        goal.get().setCurrentValue(currentValue);
        updateProgressPercentage(goal.get());
        checkGoalAchievement(goal.get());

        log.info("更新目標進度: ID={}, 當前值={}", goalId, currentValue);
        return healthGoalRepository.save(goal.get());
    }

    /**
     * 標記目標為已達成
     */
    @Transactional
    public HealthGoal markGoalAsAchieved(Long goalId) {
        Optional<HealthGoal> goal = healthGoalRepository.findById(goalId);
        if (goal.isEmpty() || goal.get().getDeleted()) {
            throw new RuntimeException("健康目標不存在: " + goalId);
        }

        goal.get().setGoalStatus("achieved");
        goal.get().setAchievedDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        goal.get().setProgressPercentage(100.0);

        log.info("標記目標已達成: ID={}", goalId);
        return healthGoalRepository.save(goal.get());
    }

    /**
     * 刪除健康目標（軟刪除）
     */
    @Transactional
    public void deleteHealthGoal(Long goalId) {
        Optional<HealthGoal> goal = healthGoalRepository.findById(goalId);
        if (goal.isPresent() && !goal.get().getDeleted()) {
            goal.get().setDeleted(true);
            goal.get().setGoalStatus("cancelled");
            healthGoalRepository.save(goal.get());
            log.info("刪除健康目標: ID={}", goalId);
        }
    }

    /**
     * 獲取目標統計
     */
    public long getGoalCountByStatus(CclemonUser user, String status) {
        return healthGoalRepository.countByUserAndGoalStatus(user, status);
    }

    /**
     * 找到已完成但未標記為達成的目標
     */
    public List<HealthGoal> findCompletedGoals(CclemonUser user) {
        return healthGoalRepository.findCompletedButNotMarkedAchievedGoals(user);
    }

    /**
     * 計算並更新進度百分比
     */
    private void updateProgressPercentage(HealthGoal goal) {
        if (goal.getTargetValue() != null && goal.getCurrentValue() != null && goal.getTargetValue() > 0) {
            double progress;
            
            // 根據目標類型計算進度
            switch (goal.getGoalType()) {
                case "weight_loss":
                    // 減重目標：進度 = (開始值 - 當前值) / (開始值 - 目標值) * 100
                    // 這裡簡化計算，假設從當前值到目標值
                    if (goal.getCurrentValue() <= goal.getTargetValue()) {
                        progress = 100.0;
                    } else {
                        progress = Math.max(0, (1 - (goal.getCurrentValue() - goal.getTargetValue()) / goal.getCurrentValue()) * 100);
                    }
                    break;
                case "weight_gain":
                    // 增重目標：進度 = (當前值 - 開始值) / (目標值 - 開始值) * 100
                    progress = Math.min(100.0, (goal.getCurrentValue() / goal.getTargetValue()) * 100);
                    break;
                default:
                    // 一般目標：進度 = 當前值 / 目標值 * 100
                    progress = Math.min(100.0, (goal.getCurrentValue() / goal.getTargetValue()) * 100);
                    break;
            }
            
            goal.setProgressPercentage(Math.max(0, progress));
        }
    }

    /**
     * 檢查目標是否達成
     */
    private void checkGoalAchievement(HealthGoal goal) {
        if (goal.getProgressPercentage() >= 100.0 && !"achieved".equals(goal.getGoalStatus())) {
            goal.setGoalStatus("achieved");
            goal.setAchievedDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            log.info("自動標記目標已達成: ID={}, 標題={}", goal.getId(), goal.getTitle());
        }
    }
}