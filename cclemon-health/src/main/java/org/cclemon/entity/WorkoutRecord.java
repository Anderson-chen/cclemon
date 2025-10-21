package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 訓練記錄
 * 記錄用戶的具體訓練數據，支援組數重量管理
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "workout_records", indexes = {
        @Index(name = "idx_user_date", columnList = "auth_user_id, workout_date"),
        @Index(name = "idx_user_exercise", columnList = "auth_user_id, exercise_id"),
        @Index(name = "idx_workout_date", columnList = "workout_date")
})
@Data
@DynamicInsert
@DynamicUpdate
public class WorkoutRecord extends BaseEntity {
    
    /**
     * 對應 cclemon-auth 的用戶ID
     */
    @Column(name = "auth_user_id", nullable = false)
    private Long authUserId;
    
    /**
     * 運動項目ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_workout_record_exercise"))
    private ExerciseItem exerciseItem;
    
    /**
     * 用戶計畫ID（可選，如果是按計畫訓練）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_plan_id",
                foreignKey = @ForeignKey(name = "fk_workout_record_user_plan"))
    private UserPlan userPlan;
    
    /**
     * 訓練日期時間
     */
    @Column(name = "workout_date", nullable = false)
    private LocalDateTime workoutDate;
    
    /**
     * 組數
     */
    @Column(name = "sets")
    private Integer sets;
    
    /**
     * 每組次數 JSON 格式
     * 例如: [12, 10, 8, 6] 表示四組分別做了12, 10, 8, 6次
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "reps_per_set")
    private Integer[] repsPerSet;
    
    /**
     * 每組重量 JSON 格式 (kg)
     * 例如: [20.0, 22.5, 25.0, 27.5]
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "weight_per_set")
    private BigDecimal[] weightPerSet;
    
    /**
     * 訓練時間（分鐘）- 用於有氧運動
     */
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    
    /**
     * 距離（公里）- 用於跑步等有氧運動
     */
    @Column(name = "distance_km", precision = 6, scale = 2)
    private BigDecimal distanceKm;
    
    /**
     * 阻力等級 - 用於器材訓練
     */
    @Column(name = "resistance_level")
    private Integer resistanceLevel;
    
    /**
     * 平均心率
     */
    @Column(name = "average_heart_rate")
    private Integer averageHeartRate;
    
    /**
     * 最大心率
     */
    @Column(name = "max_heart_rate")
    private Integer maxHeartRate;
    
    /**
     * 卡路里消耗
     */
    @Column(name = "calories_burned")
    private Integer caloriesBurned;
    
    /**
     * 個人筆記
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    /**
     * 感受評分 (1-10)
     */
    @Column(name = "feeling_score")
    private Integer feelingScore;
    
    /**
     * 困難度評分 (1-10)
     */
    @Column(name = "difficulty_score")
    private Integer difficultyScore;
    
    /**
     * 額外數據 JSON 格式
     * 用於儲存其他擴展數據
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "additional_data")
    private Map<String, Object> additionalData;
}