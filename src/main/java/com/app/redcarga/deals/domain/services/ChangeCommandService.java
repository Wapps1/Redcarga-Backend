package com.app.redcarga.deals.domain.services;

import com.app.redcarga.deals.domain.model.entities.ChangeItem;

import java.util.List;

public interface ChangeCommandService {
    /**
     * Apply a FREE change to a quote. The list of ChangeItem domain objects describes the modifications.
     * Returns the created change id.
     */
    Integer applyFreeChange(Integer quoteId, List<ChangeItem> items, Integer actorAccountId, Integer ifMatchVersion, String idempotencyKey);
}
