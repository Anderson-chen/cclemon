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
 * 食物攝取記錄
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "food_intake_records",
    indexes = {
        @Index(name = "idx_user_date", columnList = "auth_user_id, intake_date")
    }
)
@Data
@DynamicInsert
@DynamicUpdate
public class FoodIntake extends BaseEntity {
    
    /**
     * 對應 cclemon-auth 的用戶ID
     */
    @Column(name = "auth_user_id", nullable = false)
    private Long authUserId;
    
    /**
     * 攝取日期
     */
    @Column(name = "intake_date", nullable = false)
    private LocalDate intakeDate;
    
    /**
     * 攝取時間
     */
    @Column(name = "intake_time")
    private LocalDateTime intakeTime;
    
    /**
     * 食物名稱
     */
    @Column(name = "food_name", nullable = false, length = 200)
    private String foodName;
    
    /**
     * 餐點類型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type")
    private MealType mealType;
    
    /**
     * 份量 (克)
     */
    @Column(name = "serving_size_g", precision = 8, scale = 2)
    private BigDecimal servingSizeG;
    
    /**
     * 卡路里
     */
    @Column(name = "calories", precision = 8, scale = 2)
    private BigDecimal calories;
    
    /**
     * 蛋白質 (克)
     */
    @Column(name = "protein_g", precision = 6, scale = 2)
    private BigDecimal proteinG;
    
    /**
     * 碳水化合物 (克)
     */
    @Column(name = "carbs_g", precision = 6, scale = 2)
    private BigDecimal carbsG;
    
    /**
     * 脂肪 (克)
     */
    @Column(name = "fat_g", precision = 6, scale = 2)
    private BigDecimal fatG;
    
    /**
     * 備註
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    public enum MealType {
        BREAKFAST,  // 早餐
        LUNCH,      // 午餐
        DINNER,     // 晚餐
        SNACK,      // 點心
        DRINK       // 飲品
    }
}