package com.app.redcarga.deals.domain.repositories;

import com.app.redcarga.deals.domain.model.entities.AcceptanceProposal;

import java.util.Optional;

/**
 * Domain repository contract for AcceptanceProposal. Implementations belong to infrastructure.
 */
public interface AcceptanceProposalRepository {
    AcceptanceProposal save(AcceptanceProposal proposal);
    Optional<AcceptanceProposal> findById(Integer acceptanceId);
    Optional<AcceptanceProposal> findByQuoteIdAndIdempotencyKey(Integer quoteId, String idempotencyKey);
}
