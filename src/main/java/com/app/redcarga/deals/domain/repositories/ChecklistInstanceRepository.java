package com.app.redcarga.deals.domain.repositories;

import com.app.redcarga.deals.domain.model.entities.ChecklistInstance;

public interface ChecklistInstanceRepository {
    boolean existsByQuoteId(Integer quoteId);
    ChecklistInstance findByQuoteId(Integer quoteId);
    ChecklistInstance save(ChecklistInstance instance);
}
