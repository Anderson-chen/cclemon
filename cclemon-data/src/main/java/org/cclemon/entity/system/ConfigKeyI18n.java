package org.cclemon.entity.system;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "config_key_i18n")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigKeyI18n {

    @EmbeddedId
    private ConfigKeyI18nId id;

    @ManyToOne
    @MapsId("code")
    @JoinColumn(name = "code")
    private ConfigKey configKey;

    @Column(name = "display_name", length = 255)
    private String displayName;

    @Lob
    @Column(name = "description_local")
    private String descriptionLocal;
}
