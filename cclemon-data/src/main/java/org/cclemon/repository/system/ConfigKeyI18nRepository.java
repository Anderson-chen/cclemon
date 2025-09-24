package org.cclemon.repository.system;

import org.cclemon.entity.system.ConfigKeyI18n;
import org.cclemon.entity.system.ConfigKeyI18nId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigKeyI18nRepository extends JpaRepository<ConfigKeyI18n, ConfigKeyI18nId> {

    List<ConfigKeyI18n> findByIdCode(String code);

    List<ConfigKeyI18n> findByIdLocale(String locale);

}
