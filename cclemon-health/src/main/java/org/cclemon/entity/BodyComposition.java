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
 * 身體組成記錄
 * 對應 Notion 設計的身體組成數值管理規格
 * 擴展現有的 DailyInfo 概念
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "body_composition_records", 
    uniqueConstraints = {
        @UniqueConstraint(name = "unique_user_date_type", 
                         columnNames = {"auth_user_id", "measurement_date", "measurement_type"})
    },
    indexes = {
        @Index(name = "idx_user_date", columnList = "auth_user_id, measurement_date"),
        @Index(name = "idx_measurement_type", columnList = "measurement_type")
    }
)
@Data
@DynamicInsert
@DynamicUpdate
public class  BodyComposition extends BaseEntity {
    
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
     * 測量類型: WEIGHT, BODY_FAT, MUSCLE_MASS, BMI, VISCERAL_FAT
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "measurement_type", nullable = false)
    private MeasurementType measurementType;
    
    /**
     * 測量值
     */
    @Column(name = "measurement_value", nullable = false, precision = 6, scale = 2)
    private BigDecimal measurementValue;
    
    /**
     * 測量單位: KG, PERCENT, INDEX
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    private Unit unit;
    
    /**
     * 數據來源: MANUAL, SMART_SCALE, BODY_FAT_SCALE, FITNESS_TRACKER
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "data_source")
    private DataSource dataSource = DataSource.MANUAL;
    
    /**
     * 設備名稱
     */
    @Column(name = "device_name", length = 100)
    private String deviceName;
    
    /**
     * 測量備註
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    /**
     * 測量照片路徑
     */
    @Column(name = "photo_path")
    private String photoPath;
    
    /**
     * 是否為目標值
     */
    @Column(name = "is_target_value")
    private Boolean isTargetValue = false;
    
    public enum MeasurementType {
        WEIGHT,         // 體重 (kg)
        BODY_FAT,       // 體脂率 (%)
        MUSCLE_MASS,    // 肌肉量 (kg)
        BMI,            // BMI指數
        VISCERAL_FAT,   // 內臟脂肪等級
        BONE_MASS,      // 骨量 (kg)
        WATER_PERCENT,  // 水分率 (%)
        METABOLIC_AGE   // 代謝年齡
    }
    
    public enum Unit {
        KG,             // 公斤
        PERCENT,        // 百分比
        INDEX,          // 指數/等級
        YEARS           // 年齡
    }
    
    public enum DataSource {
        MANUAL,         // 手動輸入
        SMART_SCALE,    // 智能體重計
        BODY_FAT_SCALE, // 體脂計
        FITNESS_TRACKER // 健身追蹤器
    }
}