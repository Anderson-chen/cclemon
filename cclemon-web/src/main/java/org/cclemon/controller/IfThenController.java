package org.cclemon.controller;

import lombok.RequiredArgsConstructor;
import org.cclemon.api.IfThenHandler;
import org.cclemon.api.vo.IfThenResult;
import org.cclemon.api.vo.command.CreateIfThenCommand;
import org.cclemon.api.vo.command.DeleteIfThenCommand;
import org.cclemon.api.vo.command.UpdateIfThenCommand;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/if-then")
@RequiredArgsConstructor
public class IfThenController {

    private final IfThenHandler ifThenHandler;

    private Long getCurrentUserId() {
        return 1L;
    }

    @GetMapping
    public List<IfThenResult> listPlans() {
        return ifThenHandler.listPlans(getCurrentUserId());
    }

    @PostMapping
    public IfThenResult createPlan(@RequestBody IfThenRequest request) {
        return ifThenHandler.createPlan(CreateIfThenCommand.builder()
                .userId(getCurrentUserId())
                .trigger(request.trigger())
                .action(request.action())
                .category(request.category())
                .atRiskWindowStart(request.atRiskWindowStart())
                .atRiskWindowEnd(request.atRiskWindowEnd())
                .active(request.active() != null ? request.active() : true)
                .build());
    }

    @PatchMapping("/{id}")
    public IfThenResult updatePlan(@PathVariable("id") Long id, @RequestBody IfThenRequest request) {
        return ifThenHandler.updatePlan(UpdateIfThenCommand.builder()
                .userId(getCurrentUserId())
                .planId(id)
                .trigger(request.trigger())
                .action(request.action())
                .category(request.category())
                .atRiskWindowStart(request.atRiskWindowStart())
                .atRiskWindowEnd(request.atRiskWindowEnd())
                .active(request.active())
                .build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlan(@PathVariable("id") Long id) {
        ifThenHandler.deletePlan(DeleteIfThenCommand.builder()
                .userId(getCurrentUserId())
                .planId(id)
                .build());
    }

    record IfThenRequest(
            String trigger,
            String action,
            String category,
            String atRiskWindowStart,
            String atRiskWindowEnd,
            Boolean active
    ) {}
}
