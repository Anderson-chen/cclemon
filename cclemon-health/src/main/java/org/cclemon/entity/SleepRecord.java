package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 睡眠記錄
 * 對應 Notion 設計的睡眠管理系統規格
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sleep_records", 
    uniqueConstraints = {
        @UniqueConstraint(name = "unique_user_sleep_date", 
                         columnNames = {"auth_user_id", "sleep_date"})
    },
    indexes = {
        @Index(name = "idx_user_date", columnList = "auth_user_id, sleep_date"),
        @Index(name = "idx_sleep_quality", columnList = "sleep_quality_score")
    }
)
@Data
@DynamicInsert
@DynamicUpdate
public class SleepRecord extends BaseEntity {
    
    /**
     * 對應 cclemon-auth 的用戶ID
     */
    @Column(name = "auth_user_id", nullable = false)
    private Long authUserId;
    
    /**
     * 睡眠日期（以入睡日期為準）
     */
    @Column(name = "sleep_date", nullable = false)
    private LocalDate sleepDate;
    
    /**
     * 就寢時間
     */
    @Column(name = "bedtime")
    private LocalDateTime bedtime;
    
    /**
     * 入睡時間
     */
    @Column(name = "fall_asleep_time")
    private LocalDateTime fallAsleepTime;
    
    /**
     * 起床時間
     */
    @Column(name = "wake_up_time")
    private LocalDateTime wakeUpTime;
    
    /**
     * 實際睡眠時長（分鐘）
     */
    @Column(name = "total_sleep_minutes")
    private Integer totalSleepMinutes;
    
    /**
     * 深度睡眠時長（分鐘）
     */
    @Column(name = "deep_sleep_minutes")
    private Integer deepSleepMinutes;
    
    /**
     * 淺度睡眠時長（分鐘）
     */
    @Column(name = "light_sleep_minutes")
    private Integer lightSleepMinutes;
    
    /**
     * REM睡眠時長（分鐘）
     */
    @Column(name = "rem_sleep_minutes")
    private Integer remSleepMinutes;
    
    /**
     * 清醒次數
     */
    @Column(name = "awake_count")
    private Integer awakeCount;
    
    /**
     * 清醒總時長（分鐘）
     */
    @Column(name = "awake_minutes")
    private Integer awakeMinutes;
    
    /**
     * 睡眠效率（%）
     */
    @Column(name = "sleep_efficiency", precision = 5, scale = 2)
    private BigDecimal sleepEfficiency;
    
    /**
     * 睡眠品質評分 (1-10)
     */
    @Column(name = "sleep_quality_score")
    private Integer sleepQualityScore;
    
    /**
     * 主觀感受評分 (1-10)
     */
    @Column(name = "subjective_score")
    private Integer subjectiveScore;
    
    /**
     * 平均心率
     */
    @Column(name = "average_heart_rate")
    private Integer averageHeartRate;
    
    /**
     * 最低心率
     */
    @Column(name = "lowest_heart_rate")
    private Integer lowestHeartRate;
    
    /**
     * 數據來源: MANUAL, SMART_WATCH, SLEEP_TRACKER, PHONE_APP
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "data_source")
    private DataSource dataSource = DataSource.MANUAL;
    
    /**
     * 設備名稱
     */
    @Column(name = "device_name", length = 100)
    private String deviceName;
    
    /**
     * 睡眠環境因素 JSON 格式
     * 例如: {"temperature": 22, "noise_level": "quiet", "light": "dark"}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "environment_factors")
    private Map<String, Object> environmentFactors;
    
    /**
     * 睡前活動記錄 JSON 格式
     * 例如: {"caffeine": false, "exercise": true, "screen_time": 30}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "pre_sleep_activities")
    private Map<String, Object> preSleepActivities;
    
    /**
     * 備註
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    public enum DataSource {
        MANUAL,         // 手動輸入
        SMART_WATCH,    // 智慧手錫
        SLEEP_TRACKER,  // 睡眠追蹤器
        PHONE_APP       // 手機APP
    }
    
    public enum SleepQuality {
        EXCELLENT,  // 優異 (9-10分)
        GOOD,       // 良好 (7-8分)
        FAIR,       // 一般 (5-6分)
        POOR,       // 較差 (3-4分)
        VERY_POOR   // 很差 (1-2分)
    }
}
