package org.cclemon.api;

import org.cclemon.api.vo.IfThenResult;
import org.cclemon.api.vo.command.CreateIfThenCommand;
import org.cclemon.api.vo.command.DeleteIfThenCommand;
import org.cclemon.api.vo.command.UpdateIfThenCommand;

import java.util.List;

/**
 * If-Then 計劃 Handler 介面
 */
public interface IfThenHandler {

    List<IfThenResult> listPlans(Long userId);

    IfThenResult createPlan(CreateIfThenCommand command);

    IfThenResult updatePlan(UpdateIfThenCommand command);

    void deletePlan(DeleteIfThenCommand command);
}
