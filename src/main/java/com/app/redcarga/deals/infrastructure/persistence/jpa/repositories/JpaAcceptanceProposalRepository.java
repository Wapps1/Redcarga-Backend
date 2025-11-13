package com.app.redcarga.deals.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.deals.domain.model.entities.AcceptanceProposal;
import com.app.redcarga.deals.domain.repositories.AcceptanceProposalRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaAcceptanceProposalRepository
        extends JpaRepository<AcceptanceProposal, Integer>, AcceptanceProposalRepository {
    @Override
    Optional<AcceptanceProposal> findByQuoteIdAndIdempotencyKey(Integer quoteId, String idempotencyKey);
}