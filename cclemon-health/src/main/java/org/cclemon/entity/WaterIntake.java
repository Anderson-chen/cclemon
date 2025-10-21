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
 * 飲水記錄
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "water_intake_records",
    indexes = {
        @Index(name = "idx_user_date", columnList = "auth_user_id, intake_date")
    }
)
@Data
@DynamicInsert
@DynamicUpdate
public class WaterIntake extends BaseEntity {
    
    /**
     * 對應 cclemon-auth 的用戶ID
     */
    @Column(name = "auth_user_id", nullable = false)
    private Long authUserId;
    
    /**
     * 飲水日期
     */
    @Column(name = "intake_date", nullable = false)
    private LocalDate intakeDate;
    
    /**
     * 飲水時間
     */
    @Column(name = "intake_time")
    private LocalDateTime intakeTime;
    
    /**
     * 飲水量 (毫升)
     */
    @Column(name = "amount_ml", nullable = false, precision = 8, scale = 2)
    private BigDecimal amountMl;
    
    /**
     * 飲品類型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "drink_type")
    private DrinkType drinkType = DrinkType.WATER;
    
    /**
     * 備註
     */
    @Column(name = "notes", length = 500)
    private String notes;
    
    public enum DrinkType {
        WATER,      // 白開水
        TEA,        // 茶
        COFFEE,     // 咖啡
        JUICE,      // 果汁
        SPORTS,     // 運動飲料
        OTHER       // 其他
    }
}