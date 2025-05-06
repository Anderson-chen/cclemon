package org.cclemon.repository;

import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CclemonUserRepository extends JpaRepository<CclemonUser, Long> {

    
}
