package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cclemon.entity.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "water_daily_logs", uniqueConstraints = {
        @UniqueConstraint(name = "water_daily_logs_user_id_date_unique", columnNames = {"user_id", "date"})
})
@Data
public class WaterDailyLog extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "water_daily_logs_user_id_foreign"))
    private CclemonUser user;

    @Column(name = "date", nullable = false, length = 10)
    private String date;

    @Column(name = "total_ml", nullable = false)
    private Integer totalMl = 0;

    @OneToMany(mappedBy = "dailyLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WaterLogEntry> logs = new ArrayList<>();
}