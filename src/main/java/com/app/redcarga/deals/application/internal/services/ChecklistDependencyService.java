package com.app.redcarga.deals.application.internal.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.app.redcarga.deals.domain.repositories.ChecklistTemplateDependencyRepository;
import com.app.redcarga.deals.domain.repositories.ChecklistInstanceItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChecklistDependencyService {

    private final ChecklistTemplateDependencyRepository templateDependencyRepository;
    private final ChecklistInstanceItemRepository instanceItemRepository;

    public List<String> getDependsOn(Integer templateId, String itemCode) {
        return templateDependencyRepository.findDependsOnCodesByTemplateIdAndItemCode(templateId, itemCode);
    }

    /**
     * Returns list of codes that are missing (not DONE) among the given codes for the instance.
     * If empty => all complete.
     */
    public List<String> findMissingDependencies(Integer instanceId, List<String> dependsOnCodes) {
        if (dependsOnCodes == null || dependsOnCodes.isEmpty()) return java.util.List.of();
        return instanceItemRepository.findCodesNotDoneByInstanceIdAndCodes(instanceId, dependsOnCodes);
    }
}
