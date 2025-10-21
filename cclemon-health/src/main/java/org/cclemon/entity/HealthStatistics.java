package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 健康統計實體
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "health_statistics",
    indexes = {
        @Index(name = "idx_user_date", columnList = "auth_user_id, statistics_date"),
        @Index(name = "idx_statistics_type", columnList = "statistics_type")
    }
)
@Data
@DynamicInsert
@DynamicUpdate
public class HealthStatistics extends BaseEntity {
    
    /**
     * 對應 cclemon-auth 的用戶ID
     */
    @Column(name = "auth_user_id", nullable = false)
    private Long authUserId;
    
    /**
     * 統計日期
     */
    @Column(name = "statistics_date", nullable = false)
    private LocalDate statisticsDate;
    
    /**
     * 統計類型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "statistics_type", nullable = false)
    private StatisticsType statisticsType;
    
    /**
     * 統計值
     */
    @Column(name = "statistics_value", precision = 10, scale = 2)
    private BigDecimal statisticsValue;
    
    /**
     * 統計單位
     */
    @Column(name = "unit", length = 20)
    private String unit;
    
    /**
     * 統計描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /**
     * 趨勢方向
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "trend_direction")
    private TrendDirection trendDirection;
    
    public enum StatisticsType {
        WEIGHT, BODY_FAT, EXERCISE_COUNT, SLEEP_HOURS, WATER_INTAKE, CALORIES_BURNED
    }
    
    public enum PeriodType {
        DAILY, WEEKLY, MONTHLY, YEARLY
    }
    
    public enum TrendDirection {
        UP, DOWN, STABLE
    }
}