package org.cclemon.service;

import lombok.RequiredArgsConstructor;
import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.IfThenPlan;
import org.cclemon.repository.CclemonUserRepository;
import org.cclemon.repository.IfThenPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class IfThenService {

    private final IfThenPlanRepository ifThenPlanRepository;
    private final CclemonUserRepository cclemonUserRepository;

    @Transactional(readOnly = true)
    public List<IfThenPlan> listByUser(Long userId) {
        return ifThenPlanRepository.findByUser_IdAndDeletedFalse(userId);
    }

    @Transactional
    public IfThenPlan create(Long userId, String trigger, String action, String category,
                              String atRiskWindowStart, String atRiskWindowEnd, Boolean active) {
        CclemonUser user = cclemonUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        IfThenPlan plan = new IfThenPlan();
        plan.setUser(user);
        plan.setTrigger(trigger);
        plan.setAction(action);
        plan.setCategory(category);
        plan.setAtRiskWindowStart(atRiskWindowStart);
        plan.setAtRiskWindowEnd(atRiskWindowEnd);
        plan.setActive(active != null ? active : true);

        return ifThenPlanRepository.save(plan);
    }

    @Transactional
    public IfThenPlan update(Long userId, Long planId, String trigger, String action, String category,
                              String atRiskWindowStart, String atRiskWindowEnd, Boolean active) {
        IfThenPlan plan = ifThenPlanRepository.findByIdAndUser_IdAndDeletedFalse(planId, userId)
                .orElseThrow(() -> new NoSuchElementException("IfThenPlan not found: " + planId));

        if (trigger != null) plan.setTrigger(trigger);
        if (action != null) plan.setAction(action);
        if (category != null) plan.setCategory(category);
        if (atRiskWindowStart != null) plan.setAtRiskWindowStart(atRiskWindowStart);
        if (atRiskWindowEnd != null) plan.setAtRiskWindowEnd(atRiskWindowEnd);
        if (active != null) plan.setActive(active);

        return ifThenPlanRepository.save(plan);
    }

    @Transactional
    public void softDelete(Long userId, Long planId) {
        IfThenPlan plan = ifThenPlanRepository.findByIdAndUser_IdAndDeletedFalse(planId, userId)
                .orElseThrow(() -> new NoSuchElementException("IfThenPlan not found: " + planId));
        plan.setDeleted(true);
        ifThenPlanRepository.save(plan);
    }
}
