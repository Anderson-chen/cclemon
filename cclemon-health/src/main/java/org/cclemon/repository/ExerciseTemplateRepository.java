package org.cclemon.repository;

import org.cclemon.entity.ExerciseTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExerciseTemplateRepository extends JpaRepository<ExerciseTemplate, Long> {

    List<ExerciseTemplate> findByUser_IdAndDeletedFalse(Long userId);

    Optional<ExerciseTemplate> findByIdAndUser_IdAndDeletedFalse(Long id, Long userId);
}
