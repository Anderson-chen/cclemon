package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cclemon.entity.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "meals")
@Data
public class Meal extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "meals_user_id_foreign"))
    private CclemonUser user;

    @Column(name = "date", nullable = false, length = 10)
    private String date;

    @Column(name = "type", nullable = false, length = 16)
    private String type;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealFoodEntry> foods = new ArrayList<>();
}