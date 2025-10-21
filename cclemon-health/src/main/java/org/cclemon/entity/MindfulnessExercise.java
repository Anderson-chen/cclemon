package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 正念練習記錄
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "mindfulness_exercises",
    indexes = {
        @Index(name = "idx_user_date", columnList = "auth_user_id, exercise_date")
    }
)
@Data
@DynamicInsert
@DynamicUpdate
public class MindfulnessExercise extends BaseEntity {
    
    /**
     * 對應 cclemon-auth 的用戶ID
     */
    @Column(name = "auth_user_id", nullable = false)
    private Long authUserId;
    
    /**
     * 練習日期
     */
    @Column(name = "exercise_date", nullable = false)
    private LocalDate exerciseDate;
    
    /**
     * 練習時間
     */
    @Column(name = "exercise_time")
    private LocalDateTime exerciseTime;
    
    /**
     * 練習類型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "exercise_type", nullable = false)
    private ExerciseType exerciseType;
    
    /**
     * 持續時間（分鐘）
     */
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;
    
    /**
     * 練習描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /**
     * 練習心得
     */
    @Column(name = "reflection", columnDefinition = "TEXT")
    private String reflection;
    
    /**
     * 評分 (1-10)
     */
    @Column(name = "rating")
    private Integer rating;
    
    public enum ExerciseType {
        MEDITATION,     // 冥想
        BREATHING,      // 呼吸練習
        BODY_SCAN,      // 身體掃描
        WALKING,        // 行走冥想
        VISUALIZATION,  // 想像練習
        GRATITUDE,      // 感恩練習
        OTHER           // 其他
    }
}