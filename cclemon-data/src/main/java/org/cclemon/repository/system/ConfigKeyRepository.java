package org.cclemon.repository.system;

import org.cclemon.entity.system.ConfigKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigKeyRepository extends JpaRepository<ConfigKey, String> {

    List<ConfigKey> findByGroupName(String groupName);

    List<ConfigKey> findByIsDeprecatedFalse();

}