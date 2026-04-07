package org.cclemon.controller;

import lombok.RequiredArgsConstructor;
import org.cclemon.api.WorkoutHandler;
import org.cclemon.api.vo.WorkoutResult;
import org.cclemon.api.vo.command.AddWorkoutCommand;
import org.cclemon.api.vo.command.DeleteWorkoutCommand;
import org.cclemon.api.vo.query.ListWorkoutsQuery;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutHandler workoutHandler;

    private Long getCurrentUserId() {
        return 1L;
    }

    @GetMapping
    public List<WorkoutResult> listWorkouts(@RequestParam(value = "date", required = false) String date) {
        String effectiveDate = date != null ? date : LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        return workoutHandler.listWorkouts(ListWorkoutsQuery.builder()
                .userId(getCurrentUserId())
                .date(effectiveDate)
                .build());
    }

    @PostMapping
    public WorkoutResult addWorkout(@RequestBody AddWorkoutRequest request) {
        String date = request.date() != null ? request.date() : LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        List<AddWorkoutCommand.ExerciseEntryCommand> exercises = request.exercises() == null ? List.of() :
                request.exercises().stream()
                        .map(e -> AddWorkoutCommand.ExerciseEntryCommand.builder()
                                .name(e.name())
                                .sets(e.sets())
                                .reps(e.reps())
                                .weight(e.weight())
                                .durationMinutes(e.durationMinutes())
                                .isPR(e.isPR())
                                .build())
                        .toList();

        return workoutHandler.addWorkout(AddWorkoutCommand.builder()
                .userId(getCurrentUserId())
                .date(date)
                .exercises(exercises)
                .notes(request.notes())
                .totalMinutes(request.totalMinutes())
                .estimatedCaloriesBurned(request.estimatedCaloriesBurned())
                .build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWorkout(@PathVariable("id") Long id) {
        workoutHandler.deleteWorkout(DeleteWorkoutCommand.builder()
                .userId(getCurrentUserId())
                .workoutId(id)
                .build());
    }

    record AddWorkoutRequest(
            String date,
            List<ExerciseEntryRequest> exercises,
            String notes,
            Integer totalMinutes,
            Integer estimatedCaloriesBurned
    ) {}

    record ExerciseEntryRequest(
            String name,
            Integer sets,
            Integer reps,
            BigDecimal weight,
            Integer durationMinutes,
            Boolean isPR
    ) {}
}
