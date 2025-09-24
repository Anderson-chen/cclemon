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
@Table(name = "config_cache_version", uniqueConstraints = {
        @UniqueConstraint(name = "ux_cache_code_scope", columnNames = {"code", "scope_hash"})
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigCacheVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String code;

    @Column(name = "scope_hash", length = 255)
    private String scopeHash;

    @Column(name = "cache_version")
    private Long cacheVersion = 1L;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
