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
import java.util.Map;

/**
 * 用戶計畫關聯表
 * 對應 Notion 設計的 user_plans 表
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_plans", 
    uniqueConstraints = {
        @UniqueConstraint(name = "unique_active_plan", columnNames = {"auth_user_id", "plan_id", "status"})
    },
    indexes = {
        @Index(name = "idx_user_status", columnList = "auth_user_id, status"),
        @Index(name = "idx_start_date", columnList = "start_date")
    }
)
@Data
@DynamicInsert
@DynamicUpdate
public class UserPlan extends BaseEntity {
    
    /**
     * 對應 cclemon-auth 的用戶ID
     */
    @Column(name = "auth_user_id", nullable = false)
    private Long authUserId;
    
    /**
     * 計畫ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false, 
                foreignKey = @ForeignKey(name = "fk_user_plan_fitness_plan"))
    private FitnessPlan fitnessPlan;
    
    /**
     * 開始日期
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    /**
     * 結束日期
     */
    @Column(name = "end_date")
    private LocalDate endDate;
    
    /**
     * 目前進行到第幾週
     */
    @Column(name = "current_week")
    private Integer currentWeek = 1;
    
    /**
     * 狀態: ACTIVE, PAUSED, COMPLETED, CANCELLED
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PlanStatus status = PlanStatus.ACTIVE;
    
    /**
     * 進度百分比
     */
    @Column(name = "progress_percentage", precision = 5, scale = 2)
    private BigDecimal progressPercentage = BigDecimal.ZERO;
    
    /**
     * 個人筆記
     */
    @Column(name = "personal_notes", columnDefinition = "TEXT")
    private String personalNotes;
    
    /**
     * 個人化設定 JSON 格式
     * 例如: {"rest_time": 60, "weight_modifier": 0.8}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "custom_settings")
    private Map<String, Object> customSettings;
    
    public enum PlanStatus {
        ACTIVE, PAUSED, COMPLETED, CANCELLED
    }
    
    // 為向後兼容添加的 PlanType 枚舉
    public enum PlanType {
        FITNESS, DIET, WEIGHT_LOSS, MUSCLE_GAIN, CARDIO, STRENGTH
    }
}
