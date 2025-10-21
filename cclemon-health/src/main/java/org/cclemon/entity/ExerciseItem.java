package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

/**
 * 運動項目主檔
 * 擴展現有的 Exercise 概念，支援更詳細的器材和運動分類
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "exercise_items", indexes = {
        @Index(name = "idx_exercise_category", columnList = "exercise_category"),
        @Index(name = "idx_equipment_type", columnList = "equipment_type"),
        @Index(name = "idx_muscle_groups", columnList = "primary_muscle_groups")
})
@Data
@DynamicInsert
@DynamicUpdate
public class ExerciseItem extends BaseEntity {
    
    /**
     * 運動項目名稱
     */
    @Column(name = "exercise_name", nullable = false, length = 100)
    private String exerciseName;
    
    /**
     * 運動項目描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /**
     * 運動類別: FREE_WEIGHT, MACHINE, CARDIO, BODYWEIGHT, STRETCHING
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "exercise_category", nullable = false)
    private ExerciseCategory exerciseCategory;
    
    /**
     * 器材類型: DUMBBELL, BARBELL, MACHINE, TREADMILL, BIKE, NONE
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "equipment_type")
    private EquipmentType equipmentType;
    
    /**
     * 主要肌群 JSON 格式
     * 例如: ["CHEST", "SHOULDERS", "TRICEPS"]
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "primary_muscle_groups")
    private List<String> primaryMuscleGroups;
    
    /**
     * 次要肌群 JSON 格式
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "secondary_muscle_groups")
    private List<String> secondaryMuscleGroups;
    
    /**
     * 動作指導 JSON 格式
     * 包含步驟、注意事項等
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "instructions")
    private Map<String, Object> instructions;
    
    /**
     * 難度等級: BEGINNER, INTERMEDIATE, ADVANCED
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level")
    private DifficultyLevel difficultyLevel;
    
    /**
     * 卡路里消耗率（每分鐘/每次）
     */
    @Column(name = "calories_per_unit")
    private Integer caloriesPerUnit;
    
    /**
     * 記錄單位類型: REPS_WEIGHT, TIME_DISTANCE, TIME_RESISTANCE
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "record_unit_type", nullable = false)
    private RecordUnitType recordUnitType;
    
    /**
     * 是否為系統預設項目
     */
    @Column(name = "is_system_default")
    private Boolean isSystemDefault = false;
    
    /**
     * 是否啟用
     */
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    public enum ExerciseCategory {
        FREE_WEIGHT, MACHINE, CARDIO, BODYWEIGHT, STRETCHING
    }
    
    public enum EquipmentType {
        DUMBBELL, BARBELL, MACHINE, TREADMILL, BIKE, ROWING_MACHINE, NONE
    }
    
    public enum DifficultyLevel {
        BEGINNER, INTERMEDIATE, ADVANCED
    }
    
    public enum RecordUnitType {
        REPS_WEIGHT,    // 次數/重量 (重訓)
        TIME_DISTANCE,  // 時間/距離 (跑步)
        TIME_RESISTANCE // 時間/阻力 (划船機)
    }
    
    // 為向後兼容添加的 ExerciseType 枚舉
    public enum ExerciseType {
        FREE_WEIGHT, MACHINE, CARDIO, BODYWEIGHT, STRETCHING
    }
}
