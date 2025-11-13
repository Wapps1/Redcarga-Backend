package com.app.redcarga.deals.application.internal.commandservices;

import com.app.redcarga.deals.application.internal.gateways.ChatMessageGateway;
import com.app.redcarga.deals.application.internal.services.ChecklistDependencyService;
import com.app.redcarga.deals.domain.exceptions.ChecklistDependencyException;
import com.app.redcarga.deals.domain.model.aggregates.Quote;
import com.app.redcarga.deals.domain.model.entities.ChecklistInstance;
import com.app.redcarga.deals.domain.model.entities.ChecklistInstanceItem;
import com.app.redcarga.deals.domain.repositories.ChecklistInstanceItemRepository;
import com.app.redcarga.deals.domain.repositories.ChecklistInstanceRepository;
import com.app.redcarga.deals.domain.repositories.QuoteRepository;
import com.app.redcarga.deals.domain.services.PaymentCommandService;
import com.app.redcarga.deals.infrastructure.outbound.DealsChatOutboxAdapter;
import com.app.redcarga.shared.domain.exceptions.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentCommandServiceImpl implements PaymentCommandService {

    private final QuoteRepository quoteRepository;
    private final ChecklistInstanceRepository instanceRepository;
    private final ChecklistInstanceItemRepository itemRepository;
    private final ChecklistDependencyService dependencyService;
    private final ChatMessageGateway chatMessageGateway;
    private final DealsChatOutboxAdapter chatOutboxAdapter; // <-- añadido
    private final com.app.redcarga.deals.domain.services.ChecklistItemCommandService checklistItemCommandService;

    @Override
    @Transactional
    public void markPaymentMade(Integer quoteId, Integer actorAccountId) {
    // use generic checklist item service to mark the item DONE (includes dependency checks)
    checklistItemCommandService.markItemDone(quoteId, "PAYMENT_MADE", true, actorAccountId);

    Quote quote = quoteRepository.findById(quoteId).orElseThrow(() -> new DomainException("quote_not_found"));

    // persist simple system chat message for the payment_made
    int messageId = chatMessageGateway.insertSystemMessage(
        quote.getId(),
        "PAYMENT_MADE",
        null,
        null,
        "Pago realizado",
        actorAccountId
    );

    // snapshot outbox for WS push (igual que en rejectQuote)
    var dto = chatMessageGateway.findAfter(quote.getId(), messageId - 1, 1).stream().findFirst().orElse(null);
    if (dto != null) chatOutboxAdapter.persistSystemMessageOutbox(dto);
    }

    @Override
    @Transactional
    public void markPaymentConfirmed(Integer quoteId, Integer actorAccountId) {
    // mark DONE using generic service (includes dependency checks)
    checklistItemCommandService.markItemDone(quoteId, "PAYMENT_CONFIRMED", true, actorAccountId);

    Quote quote = quoteRepository.findById(quoteId).orElseThrow(() -> new DomainException("quote_not_found"));

    // persist simple system chat message for the payment_confirmed
    int messageId = chatMessageGateway.insertSystemMessage(
        quote.getId(),
        "PAYMENT_CONFIRMED",
        null,
        null,
        "Pago confirmado",
        actorAccountId
    );

    // snapshot outbox for WS push (igual que en rejectQuote)
    var dto = chatMessageGateway.findAfter(quote.getId(), messageId - 1, 1).stream().findFirst().orElse(null);
    if (dto != null) chatOutboxAdapter.persistSystemMessageOutbox(dto);
    }
}
