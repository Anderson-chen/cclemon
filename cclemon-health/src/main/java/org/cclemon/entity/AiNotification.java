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
 * AI通知記錄
 * 對應 Notion 設計的 ai_notifications 表
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ai_notifications", indexes = {
        @Index(name = "idx_user_status", columnList = "auth_user_id, status"),
        @Index(name = "idx_scheduled_at", columnList = "scheduled_at"),
        @Index(name = "idx_sent_at", columnList = "sent_at")
})
@Data
@DynamicInsert
@DynamicUpdate
public class AiNotification extends BaseEntity {
    
    /**
     * 對應 cclemon-auth 的用戶ID
     */
    @Column(name = "auth_user_id", nullable = false)
    private Long authUserId;
    
    /**
     * 通知規則ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_ai_notification_rule"))
    private NotificationRule notificationRule;
    
    /**
     * 訊息內容
     */
    @Column(name = "message_content", nullable = false, columnDefinition = "TEXT")
    private String messageContent;
    
    /**
     * 通知類型: PUSH, IN_APP, EMAIL
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;
    
    /**
     * 狀態: PENDING, SENT, DELIVERED, READ, DISMISSED
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private NotificationStatus status = NotificationStatus.PENDING;
    
    /**
     * 排程發送時間
     */
    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;
    
    /**
     * 實際發送時間
     */
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    /**
     * 閱讀時間
     */
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    /**
     * 用戶回應: IGNORED, ACKNOWLEDGED, COMPLETED_ACTION
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "user_response")
    private UserResponse userResponse;
    
    /**
     * 用戶回應的額外數據 JSON 格式
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_data")
    private Map<String, Object> responseData;
    
    public enum NotificationType {
        PUSH, IN_APP, EMAIL
    }
    
    public enum NotificationStatus {
        PENDING, SENT, DELIVERED, READ, DISMISSED
    }
    
    public enum UserResponse {
        IGNORED, ACKNOWLEDGED, COMPLETED_ACTION
    }
}