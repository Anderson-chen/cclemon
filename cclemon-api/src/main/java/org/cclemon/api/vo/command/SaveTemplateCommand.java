package org.cclemon.api.vo.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SaveTemplateCommand {
    private Long userId;
    private String name;
    private List<TemplateExerciseCommand> exercises;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class TemplateExerciseCommand {
        private String name;
        private Integer sets;
        private Integer reps;
        private BigDecimal weightKg;
    }
}
