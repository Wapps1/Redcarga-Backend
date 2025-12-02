package com.app.redcarga.deals.domain.repositories;

import com.app.redcarga.deals.domain.model.entities.ChangeItem;

import java.util.List;

public interface ChangeItemRepository {
    List<ChangeItem> findByChangeId(Integer changeId);
}