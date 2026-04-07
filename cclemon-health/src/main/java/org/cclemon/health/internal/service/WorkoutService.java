package org.cclemon.health.internal.service;

import lombok.RequiredArgsConstructor;
import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.ExerciseTemplate;
import org.cclemon.entity.TemplateExerciseEntry;
import org.cclemon.entity.UserWorkout;
import org.cclemon.entity.WorkoutExerciseEntry;
import org.cclemon.repository.CclemonUserRepository;
import org.cclemon.repository.ExerciseTemplateRepository;
import org.cclemon.repository.WorkoutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final ExerciseTemplateRepository exerciseTemplateRepository;
    private final CclemonUserRepository cclemonUserRepository;

    @Transactional(readOnly = true)
    public List<UserWorkout> listByDate(Long userId, String date) {
        return workoutRepository.findByUser_IdAndDateAndDeletedFalse(userId, date);
    }

    @Transactional
    public UserWorkout addWorkout(Long userId, String date, String notes, Integer totalMinutes,
                                  Integer estimatedCaloriesBurned, List<WorkoutExerciseEntry> exercises) {
        CclemonUser user = cclemonUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        UserWorkout workout = new UserWorkout();
        workout.setUser(user);
        workout.setDate(date);
        workout.setNotes(notes);
        workout.setTotalMinutes(totalMinutes);
        workout.setEstimatedCaloriesBurned(estimatedCaloriesBurned);
        for (WorkoutExerciseEntry e : exercises) {
            e.setWorkout(workout);
        }
        workout.setExercises(exercises);
        return workoutRepository.save(workout);
    }

    @Transactional
    public void softDelete(Long userId, Long workoutId) {
        UserWorkout workout = workoutRepository.findByIdAndUser_IdAndDeletedFalse(workoutId, userId)
                .orElseThrow(() -> new NoSuchElementException("Workout not found: " + workoutId));
        workout.setDeleted(true);
        workoutRepository.save(workout);
    }

    @Transactional(readOnly = true)
    public List<ExerciseTemplate> listTemplates(Long userId) {
        return exerciseTemplateRepository.findByUser_IdAndDeletedFalse(userId);
    }

    @Transactional
    public ExerciseTemplate saveTemplate(Long userId, String name, List<TemplateExerciseEntry> exercises) {
        CclemonUser user = cclemonUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        ExerciseTemplate template = new ExerciseTemplate();
        template.setUser(user);
        template.setName(name);
        for (TemplateExerciseEntry e : exercises) {
            e.setTemplate(template);
        }
        template.setExercises(exercises);
        return exerciseTemplateRepository.save(template);
    }

    @Transactional
    public void softDeleteTemplate(Long userId, Long templateId) {
        ExerciseTemplate template = exerciseTemplateRepository.findByIdAndUser_IdAndDeletedFalse(templateId, userId)
                .orElseThrow(() -> new NoSuchElementException("Template not found: " + templateId));
        template.setDeleted(true);
        exerciseTemplateRepository.save(template);
    }

    @Transactional(readOnly = true)
    public List<WorkoutExerciseEntry> getPersonalRecords(Long userId) {
        // Collect all PR entries from all workouts
        List<UserWorkout> allWorkouts = workoutRepository.findAll().stream()
                .filter(w -> w.getUser().getId().equals(userId)
                        && (w.getDeleted() == null || !w.getDeleted()))
                .toList();

        List<WorkoutExerciseEntry> prs = new ArrayList<>();
        for (UserWorkout workout : allWorkouts) {
            if (workout.getExercises() != null) {
                workout.getExercises().stream()
                        .filter(e -> Boolean.TRUE.equals(e.getIsPR()))
                        .forEach(prs::add);
            }
        }
        return prs;
    }
}
