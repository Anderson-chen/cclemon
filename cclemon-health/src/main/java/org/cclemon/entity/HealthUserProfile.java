package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 健康模組用戶資料表 - 與 cclemon-auth 模組的映射
 * 不包含認證相關資料，僅維護健身相關的個人資料
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "health_user_profile", uniqueConstraints = {
        @UniqueConstraint(name = "health_user_profile_auth_user_id_unique", columnNames = "auth_user_id")
})
@Data
@DynamicInsert
@DynamicUpdate
public class HealthUserProfile extends BaseEntity {
    
    /**
     * 對應 cclemon-auth 模組的用戶ID
     */
    @Column(name = "auth_user_id", nullable = false, unique = true)
    private Long authUserId;
    
    /**
     * 生日
     */
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    /**
     * 性別: MALE, FEMALE, OTHER
     */
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    /**
     * 身高 (cm)
     */
    @Column(name = "height", precision = 5, scale = 2)
    private BigDecimal height;
    
    /**
     * 活動等級: SEDENTARY, LIGHT, MODERATE, ACTIVE, VERY_ACTIVE
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level")
    private ActivityLevel activityLevel;
    
    /**
     * 健身目標: WEIGHT_LOSS, MUSCLE_GAIN, MAINTAIN, ENDURANCE, STRENGTH
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "fitness_goal")
    private FitnessGoal fitnessGoal;
    
    /**
     * 目標卡路里
     */
    @Column(name = "target_calorie")
    private Long targetCalorie;

    /**
     * 目標體重 (kg)
     */
    @Column(name = "target_weight", precision = 5, scale = 2)
    private BigDecimal targetWeight;
    
    /**
     * 時區
     */
    @Column(name = "timezone", length = 50)
    private String timezone = "UTC";
    
    /**
     * 偏好語言
     */
    @Column(name = "preferred_language", length = 10)
    private String preferredLanguage = "zh-TW";
    
    /**
     * 是否啟用狀態
     */
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    public enum Gender {
        MALE, FEMALE, OTHER
    }
    
    public enum ActivityLevel {
        SEDENTARY, LIGHT, MODERATE, ACTIVE, VERY_ACTIVE
    }
    
    public enum FitnessGoal {
        WEIGHT_LOSS, MUSCLE_GAIN, MAINTAIN, ENDURANCE, STRENGTH
    }
}
