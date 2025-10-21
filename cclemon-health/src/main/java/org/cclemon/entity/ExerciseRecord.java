package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 運動記錄
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "exercise_records",
    indexes = {
        @Index(name = "idx_user_date", columnList = "auth_user_id, exercise_date")
    }
)
@Data
@DynamicInsert
@DynamicUpdate
public class ExerciseRecord extends BaseEntity {
    
    /**
     * 對應 cclemon-auth 的用戶ID
     */
    @Column(name = "auth_user_id", nullable = false)
    private Long authUserId;
    
    /**
     * 運動日期
     */
    @Column(name = "exercise_date", nullable = false)
    private LocalDate exerciseDate;
    
    /**
     * 運動時間
     */
    @Column(name = "exercise_time")
    private LocalDateTime exerciseTime;
    
    /**
     * 運動類型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "exercise_type", nullable = false)
    private ExerciseType exerciseType;
    
    /**
     * 運動名稱
     */
    @Column(name = "exercise_name", nullable = false, length = 200)
    private String exerciseName;
    
    /**
     * 持續時間（分鐘）
     */
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    
    /**
     * 消耗卡路里
     */
    @Column(name = "calories_burned", precision = 8, scale = 2)
    private BigDecimal caloriesBurned;
    
    /**
     * 強度等級
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "intensity_level")
    private IntensityLevel intensityLevel;
    
    /**
     * 距離 (公里)
     */
    @Column(name = "distance_km", precision = 6, scale = 2)
    private BigDecimal distanceKm;
    
    /**
     * 備註
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    public enum ExerciseType {
        CARDIO,         // 有氧運動
        STRENGTH,       // 肌力訓練
        FLEXIBILITY,    // 伸展
        SPORTS,         // 運動項目
        YOGA,           // 瑜伽
        WALKING,        // 步行
        RUNNING,        // 跑步
        CYCLING,        // 騎車
        SWIMMING,       // 游泳
        OTHER           // 其他
    }
    
    public enum IntensityLevel {
        LOW,            // 低強度
        MODERATE,       // 中強度
        HIGH,           // 高強度
        VERY_HIGH       // 極高強度
    }
}