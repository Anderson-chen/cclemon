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
@Table(name = "config_key", indexes = {
        @Index(name = "idx_config_key_group", columnList = "group_name")
})
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ConfigKey {

    @Id
    @Column(length = 255)
    private String code;

    @Column(name = "group_name", length = 100)
    private String groupName;

    @Column(length = 30, nullable = false)
    private String type; // STRING, BOOLEAN, NUMBER, JSON, SECRET

    @Lob
    @Column(name = "default_value")
    private String defaultValue;

    @Lob
    private String description;

    @Column(name = "validation_schema", columnDefinition = "JSON")
    private String validationSchema;

    @Column(name = "is_secret")
    private Boolean isSecret = false;

    @Column(name = "is_deprecated")
    private Boolean isDeprecated = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}


