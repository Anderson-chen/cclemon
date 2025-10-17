package org.cclemon.repository;

import org.cclemon.entity.DailyInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DailyInfoRepository extends JpaRepository<DailyInfo, Long> {

    // 根據日期查找
    Optional<DailyInfo> findByDate(String date);

    // 根據日期範圍查找（分頁，降序）
    Page<DailyInfo> findByDateBetweenOrderByDateDesc(String startDate, String endDate, Pageable pageable);
    
    // 根據日期範圍查找（不分頁，降序）
    List<DailyInfo> findByDateBetweenOrderByDateDesc(String startDate, String endDate);
    
    // 根據日期範圍查找（不分頁，升序）
    List<DailyInfo> findByDateBetweenOrderByDateAsc(String startDate, String endDate);

    // 大於等於某日期（分頁，降序）
    Page<DailyInfo> findByDateGreaterThanEqualOrderByDateDesc(String date, Pageable pageable);
    
    // 大於等於某日期（不分頁，降序）
    List<DailyInfo> findByDateGreaterThanEqualOrderByDateDesc(String date);
    
    // 大於等於某日期（不分頁，升序）
    List<DailyInfo> findByDateGreaterThanEqualOrderByDateAsc(String date);

    // 小於等於某日期（分頁，降序）
    Page<DailyInfo> findByDateLessThanEqualOrderByDateDesc(String date, Pageable pageable);

    // 全部資料（分頁，降序）
    Page<DailyInfo> findAllByOrderByDateDesc(Pageable pageable);

    // 根據用戶ID查找
    @Query("SELECT d FROM DailyInfo d WHERE d.user.id = :userId ORDER BY d.date DESC")
    List<DailyInfo> findByUserIdOrderByDateDesc(@Param("userId") Long userId);

    // 根據用戶ID和日期範圍查找
    @Query("SELECT d FROM DailyInfo d WHERE d.user.id = :userId AND d.date BETWEEN :startDate AND :endDate ORDER BY d.date DESC")
    List<DailyInfo> findByUserIdAndDateBetweenOrderByDateDesc(
            @Param("userId") Long userId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    // 根據用戶ID和日期查找
    @Query("SELECT d FROM DailyInfo d WHERE d.user.id = :userId AND d.date = :date")
    Optional<DailyInfo> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") String date);
}