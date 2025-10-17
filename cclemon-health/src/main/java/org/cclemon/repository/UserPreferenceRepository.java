package org.cclemon.repository;

import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    
    Optional<UserPreference> findByUserAndDeletedFalse(CclemonUser user);
    
    Optional<UserPreference> findByUser(CclemonUser user);
    
    boolean existsByUser(CclemonUser user);
}