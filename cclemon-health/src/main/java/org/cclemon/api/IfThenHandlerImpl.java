package org.cclemon.api;

import lombok.RequiredArgsConstructor;
import org.cclemon.api.vo.IfThenResult;
import org.cclemon.api.vo.command.CreateIfThenCommand;
import org.cclemon.api.vo.command.DeleteIfThenCommand;
import org.cclemon.api.vo.command.UpdateIfThenCommand;
import org.cclemon.entity.IfThenPlan;
import org.cclemon.service.IfThenService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class IfThenHandlerImpl implements IfThenHandler {

    private final IfThenService ifThenService;

    @Override
    @Transactional(readOnly = true)
    public List<IfThenResult> listPlans(Long userId) {
        return ifThenService.listByUser(userId).stream().map(this::toResult).toList();
    }

    @Override
    @Transactional
    public IfThenResult createPlan(CreateIfThenCommand command) {
        IfThenPlan plan = ifThenService.create(
                command.getUserId(), command.getTrigger(), command.getAction(),
                command.getCategory(), command.getAtRiskWindowStart(),
                command.getAtRiskWindowEnd(), command.getActive()
        );
        return toResult(plan);
    }

    @Override
    @Transactional
    public IfThenResult updatePlan(UpdateIfThenCommand command) {
        IfThenPlan plan = ifThenService.update(
                command.getUserId(), command.getPlanId(), command.getTrigger(),
                command.getAction(), command.getCategory(), command.getAtRiskWindowStart(),
                command.getAtRiskWindowEnd(), command.getActive()
        );
        return toResult(plan);
    }

    @Override
    @Transactional
    public void deletePlan(DeleteIfThenCommand command) {
        ifThenService.softDelete(command.getUserId(), command.getPlanId());
    }

    private IfThenResult toResult(IfThenPlan plan) {
        return IfThenResult.builder()
                .id(String.valueOf(plan.getId()))
                .trigger(plan.getTrigger())
                .action(plan.getAction())
                .category(plan.getCategory())
                .atRiskWindowStart(plan.getAtRiskWindowStart())
                .atRiskWindowEnd(plan.getAtRiskWindowEnd())
                .active(plan.getActive())
                .build();
    }
}
