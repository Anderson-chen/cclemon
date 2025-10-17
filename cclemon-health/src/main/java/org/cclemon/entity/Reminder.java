package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "reminder")
@Data
public class Reminder extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "reminder_user_id_foreign"))
    private CclemonUser user;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "reminder_type", nullable = false)
    private String reminderType; // weight, blood_pressure, blood_sugar, exercise, medication

    @Column(name = "reminder_time", nullable = false)
    private String reminderTime; // HH:mm format

    @Column(name = "repeat_pattern")
    private String repeatPattern; // daily, weekly, monthly, custom

    @Column(name = "repeat_days")
    private String repeatDays; // JSON array for custom days: ["monday", "wednesday", "friday"]

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "last_triggered")
    private String lastTriggered; // yyyy-MM-dd format

    @Column(name = "next_trigger")
    private String nextTrigger; // yyyy-MM-dd format
}