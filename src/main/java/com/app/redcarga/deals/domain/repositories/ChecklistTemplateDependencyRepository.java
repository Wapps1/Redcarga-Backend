package com.app.redcarga.deals.domain.repositories;

import java.util.List;

public interface ChecklistTemplateDependencyRepository {
    java.util.List<String> findDependsOnCodesByTemplateIdAndItemCode(Integer templateId, String itemCode);
}
