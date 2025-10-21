package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

/**
 * AI通知規則
 * 對應 Notion 設計的 notification_rules 表
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "notification_rules", indexes = {
        @Index(name = "idx_rule_type", columnList = "rule_type"),
        @Index(name = "idx_priority", columnList = "priority")
})
@Data
@DynamicInsert
@DynamicUpdate
public class NotificationRule extends BaseEntity {
    
    /**
     * 規則名稱
     */
    @Column(name = "rule_name", nullable = false, length = 100)
    private String ruleName;
    
    /**
     * 規則類型: WORKOUT_REMINDER, PROGRESS_ENCOURAGEMENT, HEALTH_ADVICE, GOAL_CHECK
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "rule_type", nullable = false)
    private RuleType ruleType;
    
    /**
     * 觸發條件 JSON 格式
     * 例如: {"days_since_workout": 2, "progress_below": 0.3}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "trigger_conditions")
    private Map<String, Object> triggerConditions;
    
    /**
     * 訊息模板
     * 例如: "Hey {user_name}, time for your {workout_type}!"
     */
    @Column(name = "message_template", nullable = false, columnDefinition = "TEXT")
    private String messageTemplate;
    
    /**
     * 優先級: LOW, MEDIUM, HIGH
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private Priority priority = Priority.MEDIUM;
    
    /**
     * 每日最大通知頻率
     */
    @Column(name = "max_frequency_per_day")
    private Integer maxFrequencyPerDay = 1;
    
    /**
     * 目標受眾 JSON 格式
     * 例如: {"fitness_level": ["BEGINNER"], "goal": ["WEIGHT_LOSS"]}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "target_audience")
    private Map<String, Object> targetAudience;
    
    /**
     * 是否啟用
     */
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    public enum RuleType {
        WORKOUT_REMINDER, PROGRESS_ENCOURAGEMENT, HEALTH_ADVICE, GOAL_CHECK
    }
    
    public enum Priority {
        LOW, MEDIUM, HIGH
    }
}