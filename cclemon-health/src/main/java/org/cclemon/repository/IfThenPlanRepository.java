package org.cclemon.repository;

import org.cclemon.entity.IfThenPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IfThenPlanRepository extends JpaRepository<IfThenPlan, Long> {

    List<IfThenPlan> findByUser_IdAndDeletedFalse(Long userId);

    Optional<IfThenPlan> findByIdAndUser_IdAndDeletedFalse(Long id, Long userId);
}
