package org.cclemon.controller.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.HealthGoal;
import org.cclemon.service.CclemonUserService;
import org.cclemon.service.HealthGoalService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health-goals")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class HealthGoalController {

    private final HealthGoalService healthGoalService;
    private final CclemonUserService userService;

    /**
     * 獲取用戶的所有健康目標
     */
    @GetMapping
    public ResponseEntity<Page<HealthGoal>> getUserHealthGoals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            Page<HealthGoal> goals = healthGoalService.getUserHealthGoals(currentUser, page, size);
            return ResponseEntity.ok(goals);
        } catch (Exception e) {
            log.error("獲取用戶健康目標失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 獲取活躍的健康目標
     */
    @GetMapping("/active")
    public ResponseEntity<List<HealthGoal>> getActiveGoals() {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            List<HealthGoal> goals = healthGoalService.getActiveGoals(currentUser);
            return ResponseEntity.ok(goals);
        } catch (Exception e) {
            log.error("獲取活躍健康目標失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根據狀態獲取健康目標
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<HealthGoal>> getGoalsByStatus(@PathVariable String status) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            List<HealthGoal> goals = healthGoalService.getGoalsByStatus(currentUser, status);
            return ResponseEntity.ok(goals);
        } catch (Exception e) {
            log.error("根據狀態獲取健康目標失敗: {}", status, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根據類型獲取健康目標
     */
    @GetMapping("/type/{goalType}")
    public ResponseEntity<List<HealthGoal>> getGoalsByType(@PathVariable String goalType) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            List<HealthGoal> goals = healthGoalService.getGoalsByType(currentUser, goalType);
            return ResponseEntity.ok(goals);
        } catch (Exception e) {
            log.error("根據類型獲取健康目標失敗: {}", goalType, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根據目標日期獲取健康目標
     */
    @GetMapping("/target-date/{targetDate}")
    public ResponseEntity<List<HealthGoal>> getGoalsByTargetDate(@PathVariable String targetDate) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            List<HealthGoal> goals = healthGoalService.getGoalsByTargetDate(currentUser, targetDate);
            return ResponseEntity.ok(goals);
        } catch (Exception e) {
            log.error("根據目標日期獲取健康目標失敗: {}", targetDate, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 創建新健康目標
     */
    @PostMapping
    public ResponseEntity<HealthGoal> createHealthGoal(@RequestBody HealthGoal goal) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            HealthGoal createdGoal = healthGoalService.createHealthGoal(currentUser, goal);
            return ResponseEntity.ok(createdGoal);
        } catch (Exception e) {
            log.error("創建健康目標失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 更新健康目標
     */
    @PutMapping("/{id}")
    public ResponseEntity<HealthGoal> updateHealthGoal(@PathVariable Long id, @RequestBody HealthGoal goal) {
        try {
            HealthGoal updatedGoal = healthGoalService.updateHealthGoal(id, goal);
            return ResponseEntity.ok(updatedGoal);
        } catch (RuntimeException e) {
            log.error("更新健康目標失敗: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("更新健康目標失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 更新目標進度
     */
    @PatchMapping("/{id}/progress")
    public ResponseEntity<HealthGoal> updateGoalProgress(@PathVariable Long id, @RequestParam Double currentValue) {
        try {
            HealthGoal updatedGoal = healthGoalService.updateGoalProgress(id, currentValue);
            return ResponseEntity.ok(updatedGoal);
        } catch (RuntimeException e) {
            log.error("更新目標進度失敗: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("更新目標進度失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 標記目標為已達成
     */
    @PatchMapping("/{id}/achieve")
    public ResponseEntity<HealthGoal> markGoalAsAchieved(@PathVariable Long id) {
        try {
            HealthGoal achievedGoal = healthGoalService.markGoalAsAchieved(id);
            return ResponseEntity.ok(achievedGoal);
        } catch (RuntimeException e) {
            log.error("標記目標已達成失敗: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("標記目標已達成失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 刪除健康目標
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHealthGoal(@PathVariable Long id) {
        try {
            healthGoalService.deleteHealthGoal(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("刪除健康目標失敗: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 獲取目標統計
     */
    @GetMapping("/stats/{status}")
    public ResponseEntity<Long> getGoalCountByStatus(@PathVariable String status) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            long count = healthGoalService.getGoalCountByStatus(currentUser, status);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("獲取目標統計失敗: {}", status, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 找到已完成但未標記為達成的目標
     */
    @GetMapping("/completed")
    public ResponseEntity<List<HealthGoal>> findCompletedGoals() {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            List<HealthGoal> goals = healthGoalService.findCompletedGoals(currentUser);
            return ResponseEntity.ok(goals);
        } catch (Exception e) {
            log.error("查找已完成目標失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}