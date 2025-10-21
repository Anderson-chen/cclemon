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
 * 體重記錄
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "weight_records",
    indexes = {
        @Index(name = "idx_user_date", columnList = "auth_user_id, measurement_date")
    }
)
@Data
@DynamicInsert
@DynamicUpdate
public class WeightRecord extends BaseEntity {
    
    /**
     * 對應 cclemon-auth 的用戶ID
     */
    @Column(name = "auth_user_id", nullable = false)
    private Long authUserId;
    
    /**
     * 測量日期
     */
    @Column(name = "measurement_date", nullable = false)
    private LocalDate measurementDate;
    
    /**
     * 測量時間
     */
    @Column(name = "measurement_time")
    private LocalDateTime measurementTime;
    
    /**
     * 體重 (公斤)
     */
    @Column(name = "weight_kg", nullable = false, precision = 6, scale = 2)
    private BigDecimal weightKg;
    
    /**
     * 備註
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    /**
     * 數據來源
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "data_source")
    private DataSource dataSource = DataSource.MANUAL;
    
    /**
     * 設備名稱
     */
    @Column(name = "device_name", length = 100)
    private String deviceName;
    
    public enum DataSource {
        MANUAL,         // 手動輸入
        SMART_SCALE,    // 智能體重計
        FITNESS_TRACKER // 健身追蹤器
    }
}