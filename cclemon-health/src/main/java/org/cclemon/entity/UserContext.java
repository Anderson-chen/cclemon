package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

/**
 * 用戶情境設定
 * 對應 Notion 設計的 user_contexts 表
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_contexts", 
    uniqueConstraints = {
        @UniqueConstraint(name = "unique_user_trigger", columnNames = {"auth_user_id", "trigger_id"})
    },
    indexes = {
        @Index(name = "idx_user_enabled", columnList = "auth_user_id, is_enabled")
    }
)
@Data
@DynamicInsert
@DynamicUpdate
public class UserContext extends BaseEntity {
    
    /**
     * 對應 cclemon-auth 的用戶ID
     */
    @Column(name = "auth_user_id", nullable = false)
    private Long authUserId;
    
    /**
     * 觸發器ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trigger_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_user_context_trigger"))
    private ContextTrigger contextTrigger;
    
    /**
     * 是否啟用
     */
    @Column(name = "is_enabled")
    private Boolean isEnabled = true;
    
    /**
     * 用戶自定義開始時間
     */
    @Column(name = "custom_time_start")
    private LocalTime customTimeStart;
    
    /**
     * 用戶自定義結束時間
     */
    @Column(name = "custom_time_end")
    private LocalTime customTimeEnd;
    
    /**
     * 個人偏好設定 JSON 格式
     * 例如: {"reminder_intensity": "gentle", "skip_weekends": true}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "personal_preferences")
    private Map<String, Object> personalPreferences;
    
    /**
     * 最後觸發時間
     */
    @Column(name = "last_triggered_at")
    private LocalDateTime lastTriggeredAt;
    
    /**
     * 觸發次數統計
     */
    @Column(name = "trigger_count")
    private Integer triggerCount = 0;
    
    /**
     * 情境類型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "context_type")
    private ContextType contextType;
    
    /**
     * 情境時間
     */
    @Column(name = "context_time")
    private LocalDateTime contextTime;
    
    /**
     * 位置資訊
     */
    @Column(name = "location")
    private String location;
    
    /**
     * 活動資訊
     */
    @Column(name = "activity")
    private String activity;
    
    /**
     * 設備資訊 JSON 格式
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "device_info")
    private Map<String, Object> deviceInfo;
    
    public enum ContextType {
        LOCATION,       // 位置情境
        TIME,           // 時間情境
        ACTIVITY,       // 活動情境
        HEALTH,         // 健康情境
        DEVICE,         // 設備情境
        SOCIAL,         // 社交情境
        WEATHER,        // 天氣情境
        CUSTOM          // 自定義情境
    }
}
