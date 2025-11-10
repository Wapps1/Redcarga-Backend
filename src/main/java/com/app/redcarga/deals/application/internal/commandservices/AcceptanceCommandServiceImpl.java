package com.app.redcarga.deals.application.internal.commandservices;

import com.app.redcarga.deals.application.internal.gateways.ChatMessageGateway;
import com.app.redcarga.deals.domain.model.entities.AcceptanceProposal;
import com.app.redcarga.deals.domain.repositories.AcceptanceProposalRepository;
import com.app.redcarga.deals.domain.repositories.QuoteRepository;
import com.app.redcarga.deals.domain.services.AcceptanceCommandService;
import com.app.redcarga.shared.domain.exceptions.DomainException;
import com.app.redcarga.shared.events.requests.RequestAcceptedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Application service for acceptance proposal lifecycle (propose / confirm / reject). */
@Service
@RequiredArgsConstructor
public class AcceptanceCommandServiceImpl implements AcceptanceCommandService {

    private final AcceptanceProposalRepository acceptanceRepo;
    private final QuoteRepository quoteRepository;
    private final ChatMessageGateway chatMessageGateway;
    private final com.app.redcarga.deals.infrastructure.outbound.DealsChatOutboxAdapter chatOutboxAdapter;
    private final com.app.redcarga.deals.infrastructure.outbound.DealsOutboxPublisher outboxPublisher;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Integer proposeAcceptance(Integer quoteId, Integer actorAccountId, String idempotencyKey, String note) {
        // Quote existence & state
        var quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new DomainException("quote_not_found"));
        ensureAcceptanceAllowedState(quote.getStateCode());

        // Idempotency
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            var existing = acceptanceRepo.findByQuoteIdAndIdempotencyKey(quoteId, idempotencyKey.trim());
            if (existing.isPresent()) return existing.get().getAcceptanceId();
        }

        // Create & persist proposal
        var proposal = AcceptanceProposal.propose(quoteId, actorAccountId, idempotencyKey);
        proposal = acceptanceRepo.save(proposal);

        // Insert system chat message (ACCEPTANCE_REQUEST)
    int messageId = chatMessageGateway.insertSystemMessage(
        quoteId,
        "ACCEPTANCE_PROPOSED",
        proposal.getAcceptanceId(),
        note == null ? "" : normalizeNote(note),
        actorAccountId
    );
    // build enriched info payload for outbox WS message
    var info = java.util.Map.of(
        "acceptance", java.util.Map.of(
            "acceptanceId", proposal.getAcceptanceId(),
            "proposalId", proposal.getAcceptanceId(),
            "proposerAccountId", proposal.getInitiatorUserId(),
            "status", proposal.getStatus().name()
        )
    );
    snapshotOutboxEnriched(messageId, quoteId, "ACCEPTANCE_PROPOSED", info);
        return proposal.getAcceptanceId();
    }

    @Override
    @Transactional
    public void confirmAcceptance(Integer quoteId, Integer acceptanceId, Integer resolverAccountId) {
        var quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new DomainException("quote_not_found"));
        ensureAcceptanceAllowedState(quote.getStateCode());

        var proposal = acceptanceRepo.findById(acceptanceId)
                .orElseThrow(() -> new DomainException("acceptance_not_found"));
        if (!proposal.getQuoteId().equals(quoteId)) throw new DomainException("acceptance_quote_mismatch");
        try {
            proposal.confirm(resolverAccountId);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            throw new DomainException(ex.getMessage());
        }
        acceptanceRepo.save(proposal);

    // Publish event to outbox instead of calling ACL directly
    publishRequestAcceptedEvent(quote.getRequestId(), quote.getId(), acceptanceId, resolverAccountId);

    // Update quote state to ACEPTADA (idempotente)
    if (!"ACEPTADA".equals(quote.getStateCode())) {
        quote.setStateCode("ACEPTADA");
        quoteRepository.save(quote);
    }

    // System chat message (ACCEPTANCE_CONFIRMED)
    int messageId = chatMessageGateway.insertSystemMessage(
        quoteId,
        "ACCEPTANCE_CONFIRMED",
        acceptanceId,
        "",
        resolverAccountId
    );
    var info = java.util.Map.of(
        "acceptance", java.util.Map.of(
            "acceptanceId", acceptanceId,
            "proposalId", acceptanceId,
            "proposerAccountId", proposal.getInitiatorUserId(),
            "resolverAccountId", resolverAccountId,
            "status", proposal.getStatus().name(),
            "quoteState", "ACEPTADA"
        )
    );
    snapshotOutboxEnriched(messageId, quoteId, "ACCEPTANCE_CONFIRMED", info);
    }

    @Override
    @Transactional
    public void rejectAcceptance(Integer quoteId, Integer acceptanceId, Integer resolverAccountId) {
        var quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new DomainException("quote_not_found"));
        ensureAcceptanceAllowedState(quote.getStateCode());

        var proposal = acceptanceRepo.findById(acceptanceId)
                .orElseThrow(() -> new DomainException("acceptance_not_found"));
        if (!proposal.getQuoteId().equals(quoteId)) throw new DomainException("acceptance_quote_mismatch");
        try {
            proposal.reject(resolverAccountId);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            throw new DomainException(ex.getMessage());
        }
        acceptanceRepo.save(proposal);

    // System chat message (ACCEPTANCE_REJECTED)
    int messageId = chatMessageGateway.insertSystemMessage(
        quoteId,
        "ACCEPTANCE_REJECTED",
        acceptanceId,
        "",
        resolverAccountId
    );
    var info = java.util.Map.of(
        "acceptance", java.util.Map.of(
            "acceptanceId", acceptanceId,
            "proposalId", acceptanceId,
            "proposerAccountId", proposal.getInitiatorUserId(),
            "resolverAccountId", resolverAccountId,
            "status", proposal.getStatus().name()
        )
    );
    snapshotOutboxEnriched(messageId, quoteId, "ACCEPTANCE_REJECTED", info);
    }

    private void ensureAcceptanceAllowedState(String state) {
        if (state == null) throw new DomainException("quote_state_invalid");
        // Allowed: TRATO, EN_ESPERA (not already ACCEPTADA) for proposing; confirmation moves request externally
        if (!("TRATO".equals(state) || "EN_ESPERA".equals(state))) {
            throw new DomainException("acceptance_state_not_allowed");
        }
    }

    private String normalizeNote(String note) {
        note = note.trim();
        if (note.length() > 2000) throw new DomainException("acceptance_note_too_long");
        return note;
    }

    /** Snapshot inserted chat system message and persist into outbox. */
    private void snapshotOutbox(int insertedMessageId, int quoteId) {
        var dto = chatMessageGateway.findAfter(quoteId, insertedMessageId - 1, 1).stream().findFirst().orElse(null);
        if (dto != null) chatOutboxAdapter.persistSystemMessageOutbox(dto);
    }

    private void snapshotOutboxEnriched(int insertedMessageId, int quoteId, String subtype, Object info) {
        var base = chatMessageGateway.findAfter(quoteId, insertedMessageId - 1, 1).stream().findFirst().orElse(null);
        if (base != null) {
            var enriched = new com.app.redcarga.deals.interfaces.rest.responses.ChatMessageDto(
                    base.messageId(),
                    base.quoteId(),
                    base.typeCode(),
                    base.contentCode(),
                    base.body(),
                    base.mediaUrl(),
                    base.clientDedupKey(),
                    base.createdBy(),
                    base.createdAt(),
                    subtype,
                    info
            );
            chatOutboxAdapter.persistSystemMessageOutbox(enriched);
        }
    }

    private void publishRequestAcceptedEvent(Integer requestId, Integer quoteId, Integer acceptanceId, Integer resolverAccountId) {
        try {
            var evt = new RequestAcceptedEvent(
                    java.util.UUID.randomUUID().toString(),
                    java.time.Instant.now(),
                    requestId,
                    quoteId,
                    acceptanceId,
                    resolverAccountId,
                    1
            );
            String payload = objectMapper.writeValueAsString(evt);
            outboxPublisher.enqueue("requests", "request.accepted.v1", requestId, quoteId, payload);
        } catch (Exception e) {
            throw new DomainException("outbox_publish_error");
        }
    }
}