package org.cclemon.controller.rest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cclemon.entity.CclemonUser;
import org.cclemon.service.CclemonUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
@CrossOrigin
public class UserController {

    private final CclemonUserService userService;

    /**
     * 獲取當前用戶資訊（暫時用固定用戶ID=10）
     */
    @GetMapping("/me")
    public ResponseEntity<CclemonUser> getCurrentUser() {
        // 暫時使用固定的用戶ID，之後可以從JWT token或session中獲取
        Long currentUserId = 10L;
        Optional<CclemonUser> user = userService.findById(currentUserId);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根據ID獲取用戶
     */
    @GetMapping("/{id}")
    public ResponseEntity<CclemonUser> getUserById(@PathVariable Long id) {
        Optional<CclemonUser> user = userService.findById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 更新用戶資訊
     */
    @PutMapping("/{id}")
    public ResponseEntity<CclemonUser> updateUser(
            @PathVariable Long id,
            @RequestBody CclemonUser user) {
        try {
            user.setId(id);
            CclemonUser updated = userService.save(user);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("更新用戶資訊失敗", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 更新當前用戶的目標設定
     */
    @PutMapping("/me/targets")
    public ResponseEntity<CclemonUser> updateUserTargets(@RequestBody Map<String, Object> targets) {
        try {
            // 暫時使用固定的用戶ID
            Long currentUserId = 10L;
            Optional<CclemonUser> userOpt = userService.findById(currentUserId);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            CclemonUser user = userOpt.get();
            
            if (targets.containsKey("targetWeight")) {
                user.setTargetWeight(((Number) targets.get("targetWeight")).longValue());
            }
            
            if (targets.containsKey("targetCalorie")) {
                user.setTargetCalorie(((Number) targets.get("targetCalorie")).longValue());
            }

            CclemonUser updated = userService.save(user);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("更新用戶目標失敗", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 創建用戶
     */
    @PostMapping
    public ResponseEntity<CclemonUser> createUser(@RequestBody CclemonUser user) {
        try {
            CclemonUser saved = userService.save(user);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            log.error("創建用戶失敗", e);
            return ResponseEntity.badRequest().build();
        }
    }
}