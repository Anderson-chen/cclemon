package org.cclemon.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "food")
@Data
public class Food extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "food_user_id_foreign"))
    private CclemonUser user;

    private String name;

    private Long calorie;

    private Long carbs;

    private Long protein;

    private Long fat;

    private Long sugar;
}