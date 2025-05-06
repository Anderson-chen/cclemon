package org.cclemon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "cclemon_user", uniqueConstraints = {
        @UniqueConstraint(name = "cclemon_user_username_unique", columnNames = "username")
})
@Data
@DynamicInsert
@DynamicUpdate
public class CclemonUser extends BaseEntity {
    @Column(nullable = false)
    private String username;

    @Column(name = "target_calorie")
    private Long targetCalorie;

    @Column(name = "target_weight")
    private Long targetWeight;
}
