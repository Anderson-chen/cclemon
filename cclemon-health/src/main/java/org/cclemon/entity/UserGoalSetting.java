package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cclemon.entity.base.BaseEntity;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_goal_settings", uniqueConstraints = {
        @UniqueConstraint(name = "user_goal_settings_user_id_unique", columnNames = {"user_id"})
})
@Data
public class UserGoalSetting extends BaseEntity {

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "user_goal_settings_user_id_foreign"))
    private CclemonUser user;

    @Column(name = "daily_calories")
    private Integer dailyCalories;

    @Column(name = "weekly_exercise_days")
    private Integer weeklyExerciseDays;

    @Column(name = "daily_water_ml")
    private Integer dailyWaterMl;

    @Column(name = "target_weight", precision = 5, scale = 2)
    private BigDecimal targetWeight;
}