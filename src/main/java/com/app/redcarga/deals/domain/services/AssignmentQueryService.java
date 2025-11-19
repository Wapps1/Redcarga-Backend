package com.app.redcarga.deals.domain.services;

import java.util.Optional;

public interface AssignmentQueryService {
    Optional<Integer> getVersionByQuoteId(Integer quoteId);
}
