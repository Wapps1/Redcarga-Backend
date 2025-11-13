package com.app.redcarga.deals.infrastructure.persistence.jdbc;

import com.app.redcarga.deals.domain.repositories.ChecklistTemplateDependencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class PgChecklistTemplateDependencyRepository implements ChecklistTemplateDependencyRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public List<String> findDependsOnCodesByTemplateIdAndItemCode(Integer templateId, String itemCode) {
        var sql = "select depends_on_code from deals.checklist_template_dependency where template_id = :templateId and item_code = :itemCode";
        return jdbc.queryForList(sql, Map.of("templateId", templateId, "itemCode", itemCode), String.class);
    }
}
