package org.cclemon.entity.system;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "config_value_audit", indexes = {
        @Index(name = "idx_audit_code_time", columnList = "code,change_time")
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigValueAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    @ManyToOne
    @JoinColumn(name = "config_value_id")
    private ConfigValue configValue;

    @Column(length = 255)
    private String code;

    @Column(name = "scope_type", length = 30)
    private String scopeType;

    @Column(name = "scope_id", length = 255)
    private String scopeId;

    @Lob
    @Column(name = "value_string")
    private String valueString;

    @Column(name = "value_json", columnDefinition = "JSON")
    private String valueJson;

    @Column(name = "changed_by", length = 255)
    private String changedBy;

    @Column(name = "change_type", length = 30)
    private String changeType; // CREATE, UPDATE, DELETE, ACTIVATE, DEACTIVATE

    @CreationTimestamp
    @Column(name = "change_time")
    private LocalDateTime changeTime;

    @Column(name = "version")
    private Integer version;
}

