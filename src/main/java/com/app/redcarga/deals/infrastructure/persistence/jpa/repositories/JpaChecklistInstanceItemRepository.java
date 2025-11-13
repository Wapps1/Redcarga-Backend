package com.app.redcarga.deals.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.deals.domain.model.entities.ChecklistInstanceItem;
import com.app.redcarga.deals.domain.repositories.ChecklistInstanceItemRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaChecklistInstanceItemRepository extends JpaRepository<ChecklistInstanceItem, Integer>, ChecklistInstanceItemRepository {
	default java.util.List<ChecklistInstanceItem> insertAll(java.util.List<ChecklistInstanceItem> items) {
		return saveAll(items);
	}
}
