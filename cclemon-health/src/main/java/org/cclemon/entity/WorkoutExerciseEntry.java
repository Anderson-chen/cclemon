package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cclemon.entity.base.BaseEntity;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "workout_exercise_entries")
@Data
public class WorkoutExerciseEntry extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "workout_id", nullable = false, foreignKey = @ForeignKey(name = "workout_exercise_entries_workout_id_foreign"))
    private UserWorkout workout;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "sets")
    private Integer sets;

    @Column(name = "reps")
    private Integer reps;

    @Column(name = "weight_kg", precision = 6, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "is_pr", nullable = false)
    private Boolean isPR = false;
}