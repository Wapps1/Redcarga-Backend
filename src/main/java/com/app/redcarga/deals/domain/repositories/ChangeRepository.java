package com.app.redcarga.deals.domain.repositories;

import com.app.redcarga.deals.domain.model.entities.Change;
import com.app.redcarga.deals.domain.model.entities.ChangeItem;
import java.util.List;
import java.util.Optional;

public interface ChangeRepository {
    Optional<Change> findById(Integer id);
    Optional<Change> findByIdWithItems(Integer id); // para command service
    List<Change> findPendingByQuoteId(Integer quoteId);
}