package com.app.redcarga.deals.application.internal.commandservices;

import com.app.redcarga.deals.application.internal.outboundservices.notifications.NewQuoteNotification;
import com.app.redcarga.deals.application.internal.outboundservices.notifications.NotificationsPort;
import com.app.redcarga.deals.domain.model.aggregates.Quote;
import com.app.redcarga.deals.domain.model.commands.CreateQuoteCommand;
import com.app.redcarga.deals.domain.repositories.QuoteRepository;
import com.app.redcarga.deals.domain.services.QuoteCommandService;
import com.app.redcarga.deals.application.internal.outboundservices.acl.ProvidersMembershipClient;
import com.app.redcarga.deals.application.internal.gateways.ChatParticipantGateway;
import com.app.redcarga.requests.interfaces.acl.RequestFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.app.redcarga.shared.domain.exceptions.DomainException;

@Service
@RequiredArgsConstructor
public class QuoteCommandServiceImpl implements QuoteCommandService {

    private final QuoteRepository quoteRepository;
    private final ProvidersMembershipClient providersMembershipClient;
    private final NotificationsPort notificationsPort;
    private final RequestFacade requestsFacade;
    private final ChatParticipantGateway chatParticipantGateway;
    private final com.app.redcarga.deals.application.internal.gateways.ChatMessageGateway chatMessageGateway;
    private final com.app.redcarga.deals.infrastructure.outbound.DealsChatOutboxAdapter chatOutboxAdapter;

    @Override
    @Transactional
    public Integer create(CreateQuoteCommand cmd, Integer creatorAccountId) {
        // Validación: el creador debe pertenecer a la compañía
        if (!providersMembershipClient.isMemberOfCompany(cmd.companyId(), creatorAccountId)) {
            throw new DomainException("not_member_of_company");
        }
        Quote quote = Quote.create(cmd, creatorAccountId);
    quoteRepository.save(quote);

    // publish to outbox (same transaction)
    var notif = new NewQuoteNotification(
        quote.getId(),
        quote.getRequestId(),
        quote.getCompanyId(),
        quote.getTotalAmount(),
        quote.getCurrency() == null ? null : quote.getCurrency().name(),
        java.time.Instant.ofEpochMilli(quote.getCreatedAt().getTime())
    );
    notificationsPort.publishNewQuote(notif);
        return quote.getId();
    }

    @Override
    @Transactional
    public void updateItemQty(Integer quoteId, Integer requestItemId, java.math.BigDecimal qty, Integer actorAccountId) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new DomainException("quote_not_found"));
        // membership check: actor must belong to the quote's company
        if (!providersMembershipClient.isMemberOfCompany(quote.getCompanyId(), actorAccountId)) {
            throw new DomainException("not_member_of_company");
        }
        quote.updateItemQty(requestItemId, qty);
        quoteRepository.save(quote);
    }

    @Override
    @Transactional
    public void removeItem(Integer quoteId, Integer requestItemId, Integer actorAccountId) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new DomainException("quote_not_found"));
        if (!providersMembershipClient.isMemberOfCompany(quote.getCompanyId(), actorAccountId)) {
            throw new DomainException("not_member_of_company");
        }
        quote.removeItem(requestItemId);
        quoteRepository.save(quote);
    }

    @Override
    @Transactional
    public void startNegotiation(Integer quoteId, Integer actorAccountId, Integer ifMatchVersion, String idempotencyKey) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new DomainException("quote_not_found"));

        // Only the original requester of the request may start the negotiation
        if (!requestsFacade.isRequester(quote.getRequestId(), actorAccountId)) {
            throw new DomainException("not_request_owner");
        }

        // optimistic version check (If-Match)
        if (ifMatchVersion == null || !ifMatchVersion.equals(quote.getVersion())) {
            throw new org.springframework.orm.ObjectOptimisticLockingFailureException(Quote.class, quoteId);
        }

        // Domain transition
        quote.startNegotiation();
        quoteRepository.save(quote);

        // Ensure chat participants: requester (actor) and provider (quote creator)
        chatParticipantGateway.ensure(quote.getId(), actorAccountId);
        chatParticipantGateway.ensure(quote.getId(), quote.getCreatedByAccountId());

        // TODO: idempotency logging / outbox event if required
    }

    @Override
    @Transactional
    public void rejectQuote(Integer quoteId, Integer rejectedBy) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new DomainException("quote_not_found"));

        String previousState = quote.getStateCode();

        // mark this quote as RECHAZADA (idempotent)
        if (!"RECHAZADA".equals(previousState)) {
            quote.setStateCode("RECHAZADA");
            quoteRepository.save(quote);
        }

        // persist simple system chat message for the rejected quote
        int messageId = chatMessageGateway.insertSystemMessage(
                quote.getId(),
                "QUOTE_REJECTED",
                null,
                null,
                "Cotización rechazada",
                rejectedBy
        );
        // snapshot outbox for WS push
        var dto = chatMessageGateway.findAfter(quote.getId(), messageId - 1, 1).stream().findFirst().orElse(null);
        if (dto != null) chatOutboxAdapter.persistSystemMessageOutbox(dto);

        // If previously accepted, move other quotes EN_ESPERA -> TRATO
        if ("ACEPTADA".equals(previousState)) {
            Integer requestId = quote.getRequestId();
            quoteRepository.updateStateForRequestExcept(requestId, quote.getId(), "EN_ESPERA", "TRATO");
            // per requirement: do NOT notify these other quotes
        }
    }


}
