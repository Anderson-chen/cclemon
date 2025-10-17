package org.cclemon.repository;

import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.HealthReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HealthReportRepository extends JpaRepository<HealthReport, Long> {
    
    Page<HealthReport> findByUserAndDeletedFalseOrderByReportDateDesc(CclemonUser user, Pageable pageable);
    
    List<HealthReport> findByUserAndReportTypeAndDeletedFalseOrderByReportDateDesc(CclemonUser user, String reportType);
    
    @Query("SELECT r FROM HealthReport r WHERE r.user = :user AND r.reportDate BETWEEN :startDate AND :endDate AND r.deleted = false ORDER BY r.reportDate DESC")
    List<HealthReport> findByUserAndDateRangeAndDeletedFalse(@Param("user") CclemonUser user, 
                                                              @Param("startDate") String startDate, 
                                                              @Param("endDate") String endDate);
    
    Optional<HealthReport> findByUserAndReportTypeAndDateRangeStartAndDateRangeEndAndDeletedFalse(
            CclemonUser user, String reportType, String dateRangeStart, String dateRangeEnd);
    
    @Query("SELECT r FROM HealthReport r WHERE r.user = :user AND r.deleted = false ORDER BY r.reportDate DESC LIMIT 1")
    Optional<HealthReport> findLatestReportByUser(@Param("user") CclemonUser user);
    
    @Query("SELECT AVG(r.healthScore) FROM HealthReport r WHERE r.user = :user AND r.deleted = false")
    Double findAverageHealthScoreByUser(@Param("user") CclemonUser user);
    
    long countByUserAndReportTypeAndDeletedFalse(CclemonUser user, String reportType);
}