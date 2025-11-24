package org.cclemon.repository;

import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.UserWeightLog;
import org.cclemon.entity.WeightSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserWeightLogRepository extends JpaRepository<UserWeightLog, Long> {

    Optional<UserWeightLog> findByUserAndMeasureDateAndSourceAndDeletedFalse(CclemonUser user,
                                                                             LocalDate measureDate,
                                                                             WeightSource source);

    List<UserWeightLog> findByUserAndMeasureDateBetweenAndDeletedFalseOrderByMeasureDateDescMeasureTimeDesc(
            CclemonUser user,
            LocalDate startDate,
            LocalDate endDate
    );

    boolean existsByUserAndMeasureDateAndDeletedFalse(CclemonUser user, LocalDate date);
}
