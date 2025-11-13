package com.app.redcarga.deals.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.deals.domain.model.entities.ChecklistInstance;
import com.app.redcarga.deals.domain.repositories.ChecklistInstanceRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaChecklistInstanceRepository extends JpaRepository<ChecklistInstance, Integer>, ChecklistInstanceRepository {
    boolean existsByQuoteId(Integer quoteId);
    ChecklistInstance findByQuoteId(Integer quoteId);
}
