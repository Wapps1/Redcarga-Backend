package com.app.redcarga.deals.domain.services;

import com.app.redcarga.deals.domain.model.aggregates.Quote;
import com.app.redcarga.deals.domain.model.entities.Assignment;
import com.app.redcarga.deals.domain.queries.AcceptedAssignmentInfo;

import java.util.List;
import java.util.Optional;

public interface AssignmentQueryService {
    Optional<Integer> getVersionByQuoteId(Integer quoteId);
    List<AcceptedAssignmentInfo> getAcceptedAssignmentsForDriver(int requesterAccountId);

   Optional<Assignment> getAssignmentByQuoteId(Integer quoteId);
}
