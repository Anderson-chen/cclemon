package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * 情境觸發器
 * 對應 Notion 設計的 context_triggers 表
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "context_triggers", indexes = {
        @Index(name = "idx_trigger_type", columnList = "trigger_type"),
        @Index(name = "idx_time_range", columnList = "time_start, time_end")
})
@Data
@DynamicInsert
@DynamicUpdate
public class ContextTrigger extends BaseEntity {
    
    /**
     * 觸發器名稱
     */
    @Column(name = "trigger_name", nullable = false, length = 100)
    private String triggerName;
    
    /**
     * 觸發器類型: TIME_BASED, LOCATION_BASED, APP_LAUNCH, DATA_CHANGE
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", nullable = false)
    private TriggerType triggerType;
    
    /**
     * 觸發開始時間
     */
    @Column(name = "time_start")
    private LocalTime timeStart;
    
    /**
     * 觸發結束時間
     */
    @Column(name = "time_end")
    private LocalTime timeEnd;
    
    /**
     * 星期幾觸發 JSON 格式
     * 例如: ["MONDAY", "WEDNESDAY", "FRIDAY"]
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "days_of_week")
    private List<String> daysOfWeek;
    
    /**
     * 觸發條件 JSON 格式
     * 例如: {"app_launch": true, "location": "gym"}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "trigger_conditions")
    private Map<String, Object> triggerConditions;
    
    /**
     * 動作類型: SHOW_PLAN, REMIND_WORKOUT, SLEEP_PREP, PROGRESS_CHECK
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;
    
    /**
     * 動作配置 JSON 格式
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "action_config")
    private Map<String, Object> actionConfig;
    
    /**
     * 是否啟用
     */
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    public enum TriggerType {
        TIME_BASED, LOCATION_BASED, APP_LAUNCH, DATA_CHANGE
    }
    
    public enum ActionType {
        SHOW_PLAN, REMIND_WORKOUT, SLEEP_PREP, PROGRESS_CHECK
    }
}