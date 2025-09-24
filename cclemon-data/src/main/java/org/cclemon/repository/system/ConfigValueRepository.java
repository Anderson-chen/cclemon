package org.cclemon.repository.system;

import org.cclemon.entity.system.ConfigKey;
import org.cclemon.entity.system.ConfigValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigValueRepository extends JpaRepository<ConfigValue, Long> {

    List<ConfigValue> findByConfigKey(ConfigKey configKey);

    List<ConfigValue> findByConfigKeyCodeAndScopeTypeAndScopeId(String code, String scopeType, String scopeId);

    Optional<ConfigValue> findFirstByConfigKeyCodeAndScopeTypeAndScopeIdOrderByVersionDesc(String code, String scopeType, String scopeId);

    List<ConfigValue> findByIsActiveTrue();

}