package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 血糖記錄
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "glucose_records",
    indexes = {
        @Index(name = "idx_user_measurement_time", columnList = "auth_user_id, measurement_time")
    }
)
@Data
@DynamicInsert
@DynamicUpdate
public class GlucoseRecord extends BaseEntity {
    
    /**
     * 對應 cclemon-auth 的用戶ID
     */
    @Column(name = "auth_user_id", nullable = false)
    private Long authUserId;
    
    /**
     * 測量時間
     */
    @Column(name = "measurement_time", nullable = false)
    private LocalDateTime measurementTime;
    
    /**
     * 血糖值 (mg/dL)
     */
    @Column(name = "glucose_level", nullable = false, precision = 5, scale = 1)
    private BigDecimal glucoseLevel;
    
    /**
     * 測量時機
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "measurement_timing", nullable = false)
    private MeasurementTiming measurementTiming;
    
    /**
     * 測量備註
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    /**
     * 數據來源
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "data_source")
    private DataSource dataSource = DataSource.MANUAL;
    
    /**
     * 設備名稱
     */
    @Column(name = "device_name", length = 100)
    private String deviceName;
    
    public enum MeasurementTiming {
        FASTING,        // 空腹
        BEFORE_MEAL,    // 飯前
        AFTER_MEAL,     // 飯後
        BEDTIME,        // 睡前
        RANDOM          // 隨機
    }
    
    public enum DataSource {
        MANUAL,         // 手動輸入
        GLUCOSE_METER,  // 血糖儀
        CGM,            // 連續血糖監測
        SMART_WATCH     // 智能手表
    }
}