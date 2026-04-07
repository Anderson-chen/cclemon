package org.cclemon.api;

import org.cclemon.api.vo.PersonalRecordResult;
import org.cclemon.api.vo.TemplateResult;
import org.cclemon.api.vo.WorkoutResult;
import org.cclemon.api.vo.command.AddWorkoutCommand;
import org.cclemon.api.vo.command.DeleteTemplateCommand;
import org.cclemon.api.vo.command.DeleteWorkoutCommand;
import org.cclemon.api.vo.command.SaveTemplateCommand;
import org.cclemon.api.vo.query.ListWorkoutsQuery;

import java.util.List;

/**
 * 運動管理 Handler 介面
 */
public interface WorkoutHandler {

    List<WorkoutResult> listWorkouts(ListWorkoutsQuery query);

    WorkoutResult addWorkout(AddWorkoutCommand command);

    void deleteWorkout(DeleteWorkoutCommand command);

    List<TemplateResult> listTemplates(Long userId);

    TemplateResult saveTemplate(SaveTemplateCommand command);

    void deleteTemplate(DeleteTemplateCommand command);

    List<PersonalRecordResult> getPRs(Long userId);
}
