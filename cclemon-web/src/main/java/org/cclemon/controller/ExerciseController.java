package org.cclemon.controller;

import lombok.RequiredArgsConstructor;
import org.cclemon.api.WorkoutHandler;
import org.cclemon.api.vo.PersonalRecordResult;
import org.cclemon.api.vo.TemplateResult;
import org.cclemon.api.vo.command.DeleteTemplateCommand;
import org.cclemon.api.vo.command.SaveTemplateCommand;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/exercise")
@RequiredArgsConstructor
public class ExerciseController {

    private final WorkoutHandler workoutHandler;

    private Long getCurrentUserId() {
        return 1L;
    }

    @GetMapping("/templates")
    public List<TemplateResult> listTemplates() {
        return workoutHandler.listTemplates(getCurrentUserId());
    }

    @PostMapping("/templates")
    public TemplateResult saveTemplate(@RequestBody SaveTemplateRequest request) {
        List<SaveTemplateCommand.TemplateExerciseCommand> exercises = request.exercises() == null ? List.of() :
                request.exercises().stream()
                        .map(e -> SaveTemplateCommand.TemplateExerciseCommand.builder()
                                .name(e.name())
                                .sets(e.sets())
                                .reps(e.reps())
                                .weightKg(e.weightKg())
                                .build())
                        .toList();

        return workoutHandler.saveTemplate(SaveTemplateCommand.builder()
                .userId(getCurrentUserId())
                .name(request.name())
                .exercises(exercises)
                .build());
    }

    @DeleteMapping("/templates/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTemplate(@PathVariable("id") Long id) {
        workoutHandler.deleteTemplate(DeleteTemplateCommand.builder()
                .userId(getCurrentUserId())
                .templateId(id)
                .build());
    }

    @GetMapping("/prs")
    public List<PersonalRecordResult> getPRs() {
        return workoutHandler.getPRs(getCurrentUserId());
    }

    record SaveTemplateRequest(
            String name,
            List<TemplateExerciseRequest> exercises
    ) {}

    record TemplateExerciseRequest(
            String name,
            Integer sets,
            Integer reps,
            BigDecimal weightKg
    ) {}
}
