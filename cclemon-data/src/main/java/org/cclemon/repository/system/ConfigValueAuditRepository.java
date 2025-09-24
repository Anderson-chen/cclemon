package org.cclemon.repository.system;


import org.cclemon.entity.system.ConfigValueAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigValueAuditRepository extends JpaRepository<ConfigValueAudit, Long> {

    List<ConfigValueAudit> findByCodeOrderByChangeTimeDesc(String code);

}

