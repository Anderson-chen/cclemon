package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "health_goal")
@Data
public class HealthGoal extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "health_goal_user_id_foreign"))
    private CclemonUser user;

    @Column(name = "goal_type", nullable = false)
    private String goalType; // weight_loss, weight_gain, blood_pressure, blood_sugar, exercise, calorie

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "target_value")
    private Double targetValue;

    @Column(name = "current_value")
    private Double currentValue;

    @Column(name = "start_date")
    private String startDate; // yyyy-MM-dd format

    @Column(name = "target_date")
    private String targetDate; // yyyy-MM-dd format

    @Column(name = "achieved_date")
    private String achievedDate; // yyyy-MM-dd format

    @Column(name = "goal_status")
    private String goalStatus; // active, achieved, paused, cancelled

    @Column(name = "unit")
    private String unit; // kg, mmHg, mg/dL, minutes, etc.

    @Column(name = "priority_level")
    private String priorityLevel; // high, medium, low

    private String notes;

    @Column(name = "reminder_enabled")
    private Boolean reminderEnabled = false;

    @Column(name = "progress_percentage")
    private Double progressPercentage = 0.0;
}