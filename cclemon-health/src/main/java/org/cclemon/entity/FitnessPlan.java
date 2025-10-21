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
 * 訓練計畫主檔
 * 對應 Notion 設計的 fitness_plans 表
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "fitness_plans", indexes = {
        @Index(name = "idx_plan_type", columnList = "plan_type"),
        @Index(name = "idx_difficulty", columnList = "difficulty_level"),
        @Index(name = "idx_created_by", columnList = "created_by")
})
@Data
@DynamicInsert
@DynamicUpdate
public class FitnessPlan extends BaseEntity {
    
    /**
     * 計畫名稱
     */
    @Column(name = "plan_name", nullable = false, length = 100)
    private String planName;
    
    /**
     * 計畫描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /**
     * 計畫類型: STRENGTH, CARDIO, MIXED, FLEXIBILITY, SPORTS
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false)
    private PlanType planType;
    
    /**
     * 難度等級: BEGINNER, INTERMEDIATE, ADVANCED
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", nullable = false)
    private DifficultyLevel difficultyLevel;
    
    /**
     * 計畫持續週數
     */
    @Column(name = "duration_weeks", nullable = false)
    private Integer durationWeeks;
    
    /**
     * 每週訓練次數
     */
    @Column(name = "sessions_per_week", nullable = false)
    private Integer sessionsPerWeek;
    
    /**
     * 預估每次訓練時間（分鐘）
     */
    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes;
    
    /**
     * 目標設定 JSON 格式
     * 例如: {"weight_loss": 5, "muscle_gain": 2}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "target_goals")
    private Map<String, Object> targetGoals;
    
    /**
     * 創建者（教練或系統）
     * 對應 cclemon-auth 的用戶ID
     */
    @Column(name = "created_by")
    private Long createdBy;
    
    /**
     * 是否為範本
     */
    @Column(name = "is_template")
    private Boolean isTemplate = false;
    
    /**
     * 是否公開
     */
    @Column(name = "is_public")
    private Boolean isPublic = false;
    
    public enum PlanType {
        STRENGTH, CARDIO, MIXED, FLEXIBILITY, SPORTS
    }
    
    public enum DifficultyLevel {
        BEGINNER, INTERMEDIATE, ADVANCED
    }
}