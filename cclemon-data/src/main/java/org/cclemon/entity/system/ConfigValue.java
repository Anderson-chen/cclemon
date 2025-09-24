package org.cclemon.entity.system;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "config_value", indexes = {
        @Index(name = "idx_config_value_code_scope", columnList = "code,scope_type,scope_id"),
        @Index(name = "idx_config_value_active", columnList = "is_active")
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "code", nullable = false)
    private ConfigKey configKey;

    @Column(name = "scope_type", length = 30, nullable = false)
    private String scopeType; // GLOBAL, ENV, TENANT, CLIENT, USER

    @Column(name = "scope_id", length = 255)
    private String scopeId;

    @Lob
    @Column(name = "value_string")
    private String valueString;

    @Column(name = "value_json", columnDefinition = "JSON")
    private String valueJson;

    @Column(name = "value_type", length = 30)
    private String valueType;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_by", length = 255)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "version")
    private Integer version = 1;
}




