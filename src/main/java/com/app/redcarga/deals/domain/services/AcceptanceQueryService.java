package com.app.redcarga.deals.domain.services;

import com.app.redcarga.deals.domain.model.entities.AcceptanceProposal;

import java.util.List;
import java.util.Optional;

public interface AcceptanceQueryService {
    Optional<AcceptanceProposal> findById(Integer acceptanceId);
    List<AcceptanceProposal> findByQuoteId(Integer quoteId);
}
