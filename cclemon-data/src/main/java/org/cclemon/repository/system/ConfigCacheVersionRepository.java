package org.cclemon.repository.system;


import org.cclemon.entity.system.ConfigCacheVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigCacheVersionRepository extends JpaRepository<ConfigCacheVersion, Long> {

    Optional<ConfigCacheVersion> findByCodeAndScopeHash(String code, String scopeHash);

}

