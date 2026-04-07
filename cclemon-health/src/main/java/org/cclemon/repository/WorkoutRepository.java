package org.cclemon.repository;

import org.cclemon.entity.UserWorkout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutRepository extends JpaRepository<UserWorkout, Long> {

    List<UserWorkout> findByUser_IdAndDateAndDeletedFalse(Long userId, String date);

    Optional<UserWorkout> findByIdAndUser_IdAndDeletedFalse(Long id, Long userId);
}