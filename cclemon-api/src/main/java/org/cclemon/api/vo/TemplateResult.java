package org.cclemon.api.vo;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class TemplateResult {
    String id;
    String name;
    List<ExerciseEntryResult> exercises;
}
