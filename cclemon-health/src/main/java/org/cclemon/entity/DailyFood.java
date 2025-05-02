package org.cclemon.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "daily_food")
@Data
public class DailyFood extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "daily_food_user_id_foreign"))
    private CclemonUser user;

    @ManyToOne
    @JoinColumn(name = "food_id", foreignKey = @ForeignKey(name = "daily_food_food_id_foreign"))
    private Food food;

    @Column(name = "time_type")
    private String timeType;

    private Long count;

    private String date;
}
