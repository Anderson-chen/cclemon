package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cclemon.entity.base.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "meal_food_entries")
@Data
public class MealFoodEntry extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "meal_id", nullable = false, foreignKey = @ForeignKey(name = "meal_food_entries_meal_id_foreign"))
    private Meal meal;

    @Column(name = "food_id", length = 64)
    private String foodId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "calories")
    private Integer calories;

    @Column(name = "protein")
    private Integer protein;

    @Column(name = "carbs")
    private Integer carbs;

    @Column(name = "fat")
    private Integer fat;

    @Column(name = "amount")
    private Integer amount;
}