package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cclemon.entity.base.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "water_log_entries")
@Data
public class WaterLogEntry extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "daily_log_id", nullable = false, foreignKey = @ForeignKey(name = "water_log_entries_daily_log_id_foreign"))
    private WaterDailyLog dailyLog;

    @Column(name = "log_time", nullable = false, length = 8)
    private String logTime;

    @Column(name = "ml", nullable = false)
    private Integer ml;
}