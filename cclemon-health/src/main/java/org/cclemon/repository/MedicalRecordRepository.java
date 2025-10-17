package org.cclemon.repository;

import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.MedicalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    
    Page<MedicalRecord> findByUserAndDeletedFalseOrderByDateDesc(CclemonUser user, Pageable pageable);
    
    List<MedicalRecord> findByUserAndRecordTypeAndDeletedFalse(CclemonUser user, String recordType);
    
    @Query("SELECT m FROM MedicalRecord m WHERE m.user = :user AND m.date BETWEEN :startDate AND :endDate AND m.deleted = false ORDER BY m.date DESC")
    List<MedicalRecord> findByUserAndDateRangeAndDeletedFalse(@Param("user") CclemonUser user, 
                                                               @Param("startDate") String startDate, 
                                                               @Param("endDate") String endDate);
    
    List<MedicalRecord> findByUserAndIsActiveAndDeletedFalse(CclemonUser user, Boolean isActive);
    
    @Query("SELECT m FROM MedicalRecord m WHERE m.user = :user AND m.medicationName IS NOT NULL AND m.isActive = true AND m.deleted = false")
    List<MedicalRecord> findActiveMedicationsByUser(@Param("user") CclemonUser user);
    
    long countByUserAndRecordTypeAndDeletedFalse(CclemonUser user, String recordType);
}