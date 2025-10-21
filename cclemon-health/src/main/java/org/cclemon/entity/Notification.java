package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 通知實體
 * 提供通知管理功能
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "notifications", 
    indexes = {
        @Index(name = "idx_user_created", columnList = "auth_user_id, created_at"),
        @Index(name = "idx_user_read", columnList = "auth_user_id, is_read"),
        @Index(name = "idx_scheduled", columnList = "scheduled_at"),
        @Index(name = "idx_type_priority", columnList = "notification_type, priority")
    }
)
@Data
@DynamicInsert
@DynamicUpdate
public class Notification extends BaseEntity {
    
    /**
     * 對應 cclemon-auth 的用戶ID
     */
    @Column(name = "auth_user_id", nullable = false)
    private Long authUserId;
    
    /**
     * 通知類型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;
    
    /**
     * 優先級
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private Priority priority = Priority.MEDIUM;
    
    /**
     * 通知標題
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    /**
     * 通知內容
     */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    /**
     * 是否已讀
     */
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    /**
     * 已讀時間
     */
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    /**
     * 預定發送時間
     */
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;
    
    /**
     * 實際發送時間
     */
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    /**
     * 創建時間
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * 額外數據 JSON 格式
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extra_data")
    private Map<String, Object> extraData;
    
    /**
     * 關聯的觸發器ID（如果由觸發器產生）
     */
    @Column(name = "trigger_id")
    private Long triggerId;
    
    /**
     * 通知渠道
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "channel")
    private Channel channel = Channel.IN_APP;
    
    /**
     * 動作按鈕數據 JSON 格式
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "action_data")
    private Map<String, Object> actionData;
    
    public enum NotificationType {
        HEALTH_REMINDER,        // 健康提醒
        EXERCISE_REMINDER,      // 運動提醒
        MEAL_REMINDER,          // 用餐提醒
        MEDICATION_REMINDER,    // 用藥提醒
        SLEEP_REMINDER,         // 睡眠提醒
        WATER_REMINDER,         // 喝水提醒
        WEIGHT_TRACK,           // 體重追蹤
        GOAL_ACHIEVEMENT,       // 目標達成
        SYSTEM_NOTIFICATION,    // 系統通知
        SOCIAL_NOTIFICATION,    // 社交通知
        CUSTOM_REMINDER         // 自定義提醒
    }
    
    public enum Priority {
        LOW,        // 低優先級
        MEDIUM,     // 中優先級
        HIGH,       // 高優先級
        URGENT      // 緊急
    }
    
    public enum Channel {
        IN_APP,         // 應用內通知
        PUSH,           // 推播通知
        EMAIL,          // 郵件
        SMS,            // 簡訊
        WEBHOOK         // Webhook
    }
}