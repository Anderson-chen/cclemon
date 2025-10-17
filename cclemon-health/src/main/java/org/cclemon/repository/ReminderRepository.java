package org.cclemon.repository;

import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    
    List<Reminder> findByUserAndDeletedFalse(CclemonUser user);
    
    Page<Reminder> findByUserAndDeletedFalse(CclemonUser user, Pageable pageable);
    
    List<Reminder> findByUserAndIsActiveAndDeletedFalse(CclemonUser user, Boolean isActive);
    
    List<Reminder> findByUserAndReminderTypeAndDeletedFalse(CclemonUser user, String reminderType);
    
    @Query("SELECT r FROM Reminder r WHERE r.user = :user AND r.nextTrigger = :date AND r.isActive = true AND r.deleted = false")
    List<Reminder> findActiveRemindersByUserAndDate(@Param("user") CclemonUser user, @Param("date") String date);
    
    @Query("SELECT r FROM Reminder r WHERE r.nextTrigger = :date AND r.isActive = true AND r.deleted = false")
    List<Reminder> findActiveRemindersByDate(@Param("date") String date);
    
    long countByUserAndIsActiveAndDeletedFalse(CclemonUser user, Boolean isActive);
}