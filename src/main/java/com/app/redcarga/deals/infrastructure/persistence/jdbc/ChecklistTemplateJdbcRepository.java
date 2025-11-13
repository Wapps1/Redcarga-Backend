package com.app.redcarga.deals.infrastructure.persistence.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ChecklistTemplateJdbcRepository {

    private final JdbcTemplate jdbc;

    public ChecklistTemplateJdbcRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public record ChecklistTemplateDto(Integer templateId, String name) {}
    public record ChecklistTemplateItemDto(String code, String name, String actorCode, String kindCode, boolean required) {}

    public Optional<ChecklistTemplateDto> findDefaultTemplate() {
        var sql = "SELECT template_id, name FROM deals.checklist_template WHERE is_default = true AND active = true LIMIT 1";
        return jdbc.query(sql, rs -> rs.next() ? Optional.of(new ChecklistTemplateDto(rs.getInt("template_id"), rs.getString("name"))) : Optional.empty());
    }

    public List<ChecklistTemplateItemDto> findTemplateItems(Integer templateId) {
        var sql = "SELECT code, name, actor_code, kind_code, required FROM deals.checklist_template_item WHERE template_id = ? ORDER BY template_item_id";
        return jdbc.query(sql, (rs, rowNum) -> new ChecklistTemplateItemDto(
                rs.getString("code"),
                rs.getString("name"),
                rs.getString("actor_code"),
                rs.getString("kind_code"),
                rs.getBoolean("required")
        ), templateId);
    }
}
