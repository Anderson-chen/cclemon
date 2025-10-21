package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 健康指標記錄
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "health_metrics",
    indexes = {
        @Index(name = "idx_user_date_metric", columnList = "auth_user_id, metric_date, metric_type")
    }
)
@Data
@DynamicInsert
@DynamicUpdate
public class HealthMetric extends BaseEntity {
    
    /**
     * 對應 cclemon-auth 的用戶ID
     */
    @Column(name = "auth_user_id", nullable = false)
    private Long authUserId;
    
    /**
     * 指標日期
     */
    @Column(name = "metric_date", nullable = false)
    private LocalDate metricDate;
    
    /**
     * 指標類型
     */
    @Column(name = "metric_type", nullable = false, length = 50)
    private String metricType;
    
    /**
     * 指標值
     */
    @Column(name = "metric_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal metricValue;
    
    /**
     * 單位
     */
    @Column(name = "unit", length = 20)
    private String unit;
    
    /**
     * 備註
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}