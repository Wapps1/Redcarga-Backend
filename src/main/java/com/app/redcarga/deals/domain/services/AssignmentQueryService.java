package com.app.redcarga.deals.domain.services;

import com.app.redcarga.deals.domain.model.aggregates.Quote;
import com.app.redcarga.deals.domain.model.entities.Assignment;

import java.util.List;
import java.util.Optional;

public interface AssignmentQueryService {
    Optional<Integer> getVersionByQuoteId(Integer quoteId);
    List<Assignment> getAcceptedAssignmentsForDriver(int companyId, int driverId, int requesterAccountId);
}
