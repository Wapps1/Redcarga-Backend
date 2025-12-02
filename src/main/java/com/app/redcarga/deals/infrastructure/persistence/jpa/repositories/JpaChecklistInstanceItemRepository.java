package com.app.redcarga.deals.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.deals.domain.model.entities.ChecklistInstanceItem;
import com.app.redcarga.deals.domain.repositories.ChecklistInstanceItemRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JpaChecklistInstanceItemRepository extends JpaRepository<ChecklistInstanceItem, Integer>, ChecklistInstanceItemRepository {
	default List<ChecklistInstanceItem> insertAll(List<ChecklistInstanceItem> items) {
		return saveAll(items);
	}

	@Override
	@Query("select i from ChecklistInstanceItem i where i.instance.instanceId = :instanceId and i.code = :code")
	Optional<ChecklistInstanceItem> findByInstanceIdAndCode(Integer instanceId, String code);

	// Soporta también el nombre alterno del contrato (nested path explícito)
	@Override
	@Query("select i from ChecklistInstanceItem i where i.instance.instanceId = :instanceId and i.code = :code")
	Optional<ChecklistInstanceItem> findByInstanceInstanceIdAndCode(Integer instanceId, String code);

	// FIX: usar instance.instanceId para resolver los códigos no-DONE
	@Override
	@Query("""
           select i.code
           from ChecklistInstanceItem i
           where i.instance.instanceId = :instanceId
             and i.code in :codes
             and i.statusCode <> 'DONE'
           """)
	List<String> findCodesNotDoneByInstanceIdAndCodes(Integer instanceId,List<String> codes);

}
