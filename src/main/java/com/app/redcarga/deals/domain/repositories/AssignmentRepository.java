package com.app.redcarga.deals.domain.repositories;

import com.app.redcarga.deals.domain.model.entities.Assignment;
import java.util.Optional;

public interface AssignmentRepository {
    Assignment save(Assignment a);
    Optional<Assignment> findByQuoteId(Integer quoteId);
    void deleteById(Integer assignmentId);
}
