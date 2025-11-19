package com.app.redcarga.deals.domain.services;

import java.util.List;

public interface ChecklistDependencyQueryService {
    List<String> getDependsOn(Integer templateId, String itemCode);
    List<String> findMissingDependencies(Integer instanceId, List<String> dependsOnCodes);
}
