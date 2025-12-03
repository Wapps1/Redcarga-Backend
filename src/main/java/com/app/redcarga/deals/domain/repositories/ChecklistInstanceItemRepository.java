package com.app.redcarga.deals.domain.repositories;

import com.app.redcarga.deals.domain.model.entities.ChecklistInstanceItem;

import java.util.List;
import java.util.Optional;

public interface ChecklistInstanceItemRepository {
	List<ChecklistInstanceItem> insertAll(List<ChecklistInstanceItem> items);

	Optional<ChecklistInstanceItem> findByInstanceIdAndCode(Integer instanceId, String code);

	ChecklistInstanceItem save(ChecklistInstanceItem item);

	List<String> findCodesNotDoneByInstanceIdAndCodes(Integer instanceId, java.util.List<String> codes);

	Optional<ChecklistInstanceItem> findByInstanceInstanceIdAndCode(Integer instanceId, String code);
    
	List<ChecklistInstanceItem> findAllByInstanceId(Integer instanceId);
}
