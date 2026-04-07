package org.cclemon.health.api;

import lombok.RequiredArgsConstructor;
import org.cclemon.api.WorkoutHandler;
import org.cclemon.api.vo.ExerciseEntryResult;
import org.cclemon.api.vo.PersonalRecordResult;
import org.cclemon.api.vo.TemplateResult;
import org.cclemon.api.vo.WorkoutResult;
import org.cclemon.api.vo.command.AddWorkoutCommand;
import org.cclemon.api.vo.command.DeleteTemplateCommand;
import org.cclemon.api.vo.command.DeleteWorkoutCommand;
import org.cclemon.api.vo.command.SaveTemplateCommand;
import org.cclemon.api.vo.query.ListWorkoutsQuery;
import org.cclemon.entity.ExerciseTemplate;
import org.cclemon.entity.TemplateExerciseEntry;
import org.cclemon.entity.UserWorkout;
import org.cclemon.entity.WorkoutExerciseEntry;
import org.cclemon.health.internal.service.WorkoutService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WorkoutHandlerImpl implements WorkoutHandler {

    private final WorkoutService workoutService;

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutResult> listWorkouts(ListWorkoutsQuery query) {
        return workoutService.listByDate(query.getUserId(), query.getDate())
                .stream().map(this::toWorkoutResult).toList();
    }

    @Override
    @Transactional
    public WorkoutResult addWorkout(AddWorkoutCommand command) {
        List<WorkoutExerciseEntry> exercises = new ArrayList<>();
        if (command.getExercises() != null) {
            for (AddWorkoutCommand.ExerciseEntryCommand e : command.getExercises()) {
                WorkoutExerciseEntry entry = new WorkoutExerciseEntry();
                entry.setName(e.getName());
                entry.setSets(e.getSets());
                entry.setReps(e.getReps());
                entry.setWeightKg(e.getWeight());
                entry.setDurationMinutes(e.getDurationMinutes());
                entry.setIsPR(Boolean.TRUE.equals(e.getIsPR()));
                exercises.add(entry);
            }
        }
        UserWorkout workout = workoutService.addWorkout(
                command.getUserId(), command.getDate(), command.getNotes(),
                command.getTotalMinutes(), command.getEstimatedCaloriesBurned(), exercises);
        return toWorkoutResult(workout);
    }

    @Override
    @Transactional
    public void deleteWorkout(DeleteWorkoutCommand command) {
        workoutService.softDelete(command.getUserId(), command.getWorkoutId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TemplateResult> listTemplates(Long userId) {
        return workoutService.listTemplates(userId).stream().map(this::toTemplateResult).toList();
    }

    @Override
    @Transactional
    public TemplateResult saveTemplate(SaveTemplateCommand command) {
        List<TemplateExerciseEntry> exercises = new ArrayList<>();
        if (command.getExercises() != null) {
            for (SaveTemplateCommand.TemplateExerciseCommand e : command.getExercises()) {
                TemplateExerciseEntry entry = new TemplateExerciseEntry();
                entry.setName(e.getName());
                entry.setSets(e.getSets());
                entry.setReps(e.getReps());
                entry.setWeightKg(e.getWeightKg());
                exercises.add(entry);
            }
        }
        ExerciseTemplate template = workoutService.saveTemplate(command.getUserId(), command.getName(), exercises);
        return toTemplateResult(template);
    }

    @Override
    @Transactional
    public void deleteTemplate(DeleteTemplateCommand command) {
        workoutService.softDeleteTemplate(command.getUserId(), command.getTemplateId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonalRecordResult> getPRs(Long userId) {
        return workoutService.getPersonalRecords(userId).stream()
                .map(e -> PersonalRecordResult.builder()
                        .exerciseName(e.getName())
                        .weight(e.getWeightKg())
                        .reps(e.getReps())
                        .date(e.getWorkout() != null ? e.getWorkout().getDate() : null)
                        .build())
                .toList();
    }

    private WorkoutResult toWorkoutResult(UserWorkout workout) {
        List<ExerciseEntryResult> exercises = workout.getExercises() == null ? List.of() :
                workout.getExercises().stream()
                        .map(e -> ExerciseEntryResult.builder()
                                .name(e.getName())
                                .sets(e.getSets())
                                .reps(e.getReps())
                                .weight(e.getWeightKg())
                                .durationMinutes(e.getDurationMinutes())
                                .isPR(e.getIsPR())
                                .build())
                        .toList();

        return WorkoutResult.builder()
                .id(String.valueOf(workout.getId()))
                .userId(String.valueOf(workout.getUser().getId()))
                .date(workout.getDate())
                .exercises(exercises)
                .notes(workout.getNotes())
                .totalMinutes(workout.getTotalMinutes())
                .estimatedCaloriesBurned(workout.getEstimatedCaloriesBurned())
                .build();
    }

    private TemplateResult toTemplateResult(ExerciseTemplate template) {
        List<ExerciseEntryResult> exercises = template.getExercises() == null ? List.of() :
                template.getExercises().stream()
                        .map(e -> ExerciseEntryResult.builder()
                                .name(e.getName())
                                .sets(e.getSets())
                                .reps(e.getReps())
                                .weight(e.getWeightKg())
                                .build())
                        .toList();

        return TemplateResult.builder()
                .id(String.valueOf(template.getId()))
                .name(template.getName())
                .exercises(exercises)
                .build();
    }
}
