package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 心情記錄
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "mood_records",
    indexes = {
        @Index(name = "idx_user_date", columnList = "auth_user_id, record_date")
    }
)
@Data
@DynamicInsert
@DynamicUpdate
public class MoodRecord extends BaseEntity {
    
    /**
     * 對應 cclemon-auth 的用戶ID
     */
    @Column(name = "auth_user_id", nullable = false)
    private Long authUserId;
    
    /**
     * 記錄日期
     */
    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;
    
    /**
     * 記錄時間
     */
    @Column(name = "record_time")
    private LocalDateTime recordTime;
    
    /**
     * 心情評分 (1-10)
     */
    @Column(name = "mood_score", nullable = false)
    private Integer moodScore;
    
    /**
     * 心情描述
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "mood_type")
    private MoodType moodType;
    
    /**
     * 影響因素
     */
    @Column(name = "factors", columnDefinition = "TEXT")
    private String factors;
    
    /**
     * 備註
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    /**
     * 能量水平 (1-10)
     */
    @Column(name = "energy_level")
    private Integer energyLevel;
    
    /**
     * 壓力水平 (1-10)
     */
    @Column(name = "stress_level")
    private Integer stressLevel;
    
    public enum MoodType {
        VERY_HAPPY,     // 非常快樂
        HAPPY,          // 快樂
        CONTENT,        // 滿足
        NEUTRAL,        // 中性
        ANXIOUS,        // 焦慮
        SAD,            // 悲傷
        ANGRY,          // 生氣
        DEPRESSED,      // 沮喪
        EXCITED,        // 興奮
        STRESSED        // 壓力大
    }
}