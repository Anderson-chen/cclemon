package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cclemon.entity.base.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "if_then_plans")
@Data
public class IfThenPlan extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "if_then_plans_user_id_foreign"))
    private CclemonUser user;

    @Column(name = "trigger_text", nullable = false, length = 500)
    private String trigger;

    @Column(name = "action_text", nullable = false, length = 500)
    private String action;

    @Column(name = "category", nullable = false, length = 16)
    private String category;

    @Column(name = "at_risk_window_start", length = 8)
    private String atRiskWindowStart;

    @Column(name = "at_risk_window_end", length = 8)
    private String atRiskWindowEnd;

    @Column(name = "active", nullable = false)
    private Boolean active = true;
}