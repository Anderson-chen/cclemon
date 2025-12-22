package org.cclemon.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cclemon.entity.base.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "daily_exercise", uniqueConstraints = {
        @UniqueConstraint(name = "daily_exercise_user_id_exercise_id_date_unique", columnNames = {"user_id", "exercise_id", "date"})
})
@Data
public class DailyExercise extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "daily_exercise_user_id_foreign"))
    private CclemonUser user;


    @ManyToOne
    @JoinColumn(name = "exercise_id", foreignKey = @ForeignKey(name = "daily_exercise_exercise_id_foreign"))
    private Exercise exercise;

    private String date;

    private Long count;

    @Column(name = "photo_path")
    private String photoPath;
}