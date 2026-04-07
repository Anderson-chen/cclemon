package org.cclemon.repository;

import org.cclemon.entity.WaterDailyLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WaterDailyLogRepository extends JpaRepository<WaterDailyLog, Long> {

    Optional<WaterDailyLog> findByUser_IdAndDate(Long userId, String date);
}