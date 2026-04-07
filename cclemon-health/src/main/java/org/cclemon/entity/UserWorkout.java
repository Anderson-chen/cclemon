package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cclemon.entity.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_workouts")
@Data
public class UserWorkout extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "user_workouts_user_id_foreign"))
    private CclemonUser user;

    @Column(name = "date", nullable = false, length = 10)
    private String date;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "total_minutes")
    private Integer totalMinutes;

    @Column(name = "estimated_calories_burned")
    private Integer estimatedCaloriesBurned;

    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkoutExerciseEntry> exercises = new ArrayList<>();
}