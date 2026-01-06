package org.cclemon.repository;

import org.cclemon.entity.UserWeightLog;
import org.cclemon.entity.WeightSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserWeightLogRepository extends JpaRepository<UserWeightLog, Long> {

    // 使用 User_Id 來明確指定關聯物件的 ID 屬性
    Optional<UserWeightLog> findByUser_IdAndMeasureDateAndSourceAndDeletedFalse(Long userId,
                                                                                LocalDate measureDate,
                                                                                WeightSource source);

    Page<UserWeightLog> findByUser_IdAndMeasureDateBetweenAndDeletedFalseOrderByMeasureDateDesc(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );
    
    List<UserWeightLog> findByUser_IdAndMeasureDateBetweenAndDeletedFalseOrderByMeasureDateDesc(
        Long userId,
        LocalDate startDate,
        LocalDate endDate
    );

    boolean existsByUser_IdAndMeasureDateAndDeletedFalse(Long userId, LocalDate date);

    Optional<UserWeightLog> findByIdAndUser_Id(Long id, Long userId);
}
