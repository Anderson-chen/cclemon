package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "daily_info", uniqueConstraints = {
        @UniqueConstraint(name = "daily_info_user_id_date_unique", columnNames = {"user_id", "date"})
})
@Data
public class DailyInfo extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "daily_info_user_id_foreign"))
    private CclemonUser user;

    private String date;

    private Long weight;

    @Column(name = "systolic_pressure")
    private Long systolicPressure;

    @Column(name = "diastolic_pressure")
    private Long diastolicPressure;

    @Column(name = "blood_sugar")
    private Long bloodSugar;

    @Column(name = "blood_sugar_type")
    private String bloodSugarType;

    private String notes;

    @Column(name = "photo_path")
    private String photoPath;
}