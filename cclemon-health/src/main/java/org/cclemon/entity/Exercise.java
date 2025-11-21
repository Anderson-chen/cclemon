package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "exercise", uniqueConstraints = {
        @UniqueConstraint(name = "exercise_user_id_name_unique", columnNames = {"user_id", "name"})
})
@Data
public class Exercise extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "exercise_user_id_foreign"))
    private CclemonUser user;

    private String name;

    private String unit;

    private Long calorie;

    @Column(name = "photo_path")
    private String photoPath;
}