package org.cclemon.repository;

import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.HealthGoal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthGoalRepository extends JpaRepository<HealthGoal, Long> {
    
    Page<HealthGoal> findByUserAndDeletedFalseOrderByCreateTimeDesc(CclemonUser user, Pageable pageable);
    
    List<HealthGoal> findByUserAndGoalStatusAndDeletedFalse(CclemonUser user, String goalStatus);
    
    List<HealthGoal> findByUserAndGoalTypeAndDeletedFalse(CclemonUser user, String goalType);
    
    @Query("SELECT g FROM HealthGoal g WHERE g.user = :user AND g.goalStatus = 'active' AND g.deleted = false ORDER BY g.priorityLevel DESC, g.targetDate ASC")
    List<HealthGoal> findActiveGoalsByUserOrderByPriorityAndDate(@Param("user") CclemonUser user);
    
    @Query("SELECT g FROM HealthGoal g WHERE g.user = :user AND g.targetDate = :date AND g.goalStatus = 'active' AND g.deleted = false")
    List<HealthGoal> findActiveGoalsByUserAndTargetDate(@Param("user") CclemonUser user, @Param("date") String date);
    
    @Query("SELECT COUNT(g) FROM HealthGoal g WHERE g.user = :user AND g.goalStatus = :status AND g.deleted = false")
    long countByUserAndGoalStatus(@Param("user") CclemonUser user, @Param("status") String status);
    
    @Query("SELECT g FROM HealthGoal g WHERE g.user = :user AND g.progressPercentage >= 100 AND g.goalStatus != 'achieved' AND g.deleted = false")
    List<HealthGoal> findCompletedButNotMarkedAchievedGoals(@Param("user") CclemonUser user);
}