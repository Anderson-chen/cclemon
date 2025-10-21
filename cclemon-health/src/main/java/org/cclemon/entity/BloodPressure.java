package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

/**
 * 血壓記錄
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "blood_pressure_records",
    indexes = {
        @Index(name = "idx_user_measurement_time", columnList = "auth_user_id, measurement_time")
    }
)
@Data
@DynamicInsert
@DynamicUpdate
public class BloodPressure extends BaseEntity {
    
    /**
     * 對應 cclemon-auth 的用戶ID
     */
    @Column(name = "auth_user_id", nullable = false)
    private Long authUserId;
    
    /**
     * 測量時間
     */
    @Column(name = "measurement_time", nullable = false)
    private LocalDateTime measurementTime;
    
    /**
     * 收縮壓 (mmHg)
     */
    @Column(name = "systolic_pressure", nullable = false)
    private Integer systolicPressure;
    
    /**
     * 舒張壓 (mmHg)
     */
    @Column(name = "diastolic_pressure", nullable = false)
    private Integer diastolicPressure;
    
    /**
     * 脈搏
     */
    @Column(name = "pulse")
    private Integer pulse;
    
    /**
     * 測量備註
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
        BLOOD_PRESSURE_MONITOR, // 血壓計
        SMART_WATCH,    // 智能手表
        FITNESS_TRACKER // 健身追蹤器
    }
}