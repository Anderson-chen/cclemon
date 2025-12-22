package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cclemon.entity.base.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_weight_logs", uniqueConstraints = {
        @UniqueConstraint(name = "user_weight_logs_user_id_date_source_unique", columnNames = {"user_id", "measure_date", "source"})
})
@Data
public class UserWeightLog extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "user_weight_logs_user_id_foreign"))
    private CclemonUser user;

    @Column(name = "measure_date", nullable = false)
    private LocalDate measureDate;

    @Column(name = "measure_time")
    private LocalTime measureTime;

    @Column(name = "weight_kg", nullable = false, precision = 5, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "note", length = 255)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 16)
    private WeightSource source = WeightSource.MANUAL;
}
