package com.app.redcarga.deals.application.internal.commandservices;

import com.app.redcarga.deals.application.internal.gateways.ChatMessageGateway;
import com.app.redcarga.deals.domain.model.entities.Change;
import com.app.redcarga.deals.domain.model.entities.ChangeItem;
import com.app.redcarga.deals.domain.model.aggregates.Quote;
import com.app.redcarga.deals.domain.services.ChangeCommandService;
import com.app.redcarga.deals.infrastructure.persistence.jpa.repositories.JpaChangeRepository;
import com.app.redcarga.deals.infrastructure.outbound.DealsChangeOutboxAdapter;
import com.app.redcarga.deals.domain.repositories.QuoteRepository;
import com.app.redcarga.deals.application.internal.outboundservices.acl.ProvidersMembershipClient;
import com.app.redcarga.requests.interfaces.acl.RequestFacade;
import com.app.redcarga.shared.domain.exceptions.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChangeCommandServiceImpl implements ChangeCommandService {

    private final QuoteRepository quoteRepository;
    private final ProvidersMembershipClient providersMembershipClient;
    private final RequestFacade requestsFacade;
    private final JpaChangeRepository changeRepository;
    private final ChatMessageGateway chatMessageGateway;
    private final DealsChangeOutboxAdapter outboxAdapter;
    private final com.app.redcarga.deals.application.internal.gateways.ChatParticipantGateway chatParticipantGateway;

    @Override
    @Transactional
    public Integer applyFreeChange(Integer quoteId, List<ChangeItem> items, Integer actorAccountId, Integer ifMatchVersion, String idempotencyKey) {
        // restore original applyFreeChange behavior: permission is requester or provider member, only TRATO/EN_ESPERA
        Quote quote = quoteRepository.findById(quoteId).orElseThrow(() -> new DomainException("quote_not_found"));

        // permission: either requester or member of provider company
        boolean isRequester = requestsFacade.isRequester(quote.getRequestId(), actorAccountId);
        boolean isProviderMember = providersMembershipClient.isMemberOfCompany(quote.getCompanyId(), actorAccountId);
        if (!isRequester && !isProviderMember) throw new DomainException("not_allowed_to_change_quote");

        // Only allow LIBRE application in TRATO or EN_ESPERA
        String s = quote.getStateCode();
        if (!("TRATO".equals(s) || "EN_ESPERA".equals(s))) {
            throw new DomainException("change_not_allowed_in_state");
        }

        // optimistic check
        if (ifMatchVersion != null && !ifMatchVersion.equals(quote.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(Quote.class, quoteId);
        }

        // create Change entity (APLICADO)
        Change change = Change.createApplied(quote, "LIBRE", actorAccountId);

        // attach items to change
        for (ChangeItem ci : items) {
            change.addItem(ci);
        }

        // apply items on aggregate
        applyChangesToQuote(quote, items);

        // persist quote (mutations)
        quoteRepository.save(quote);

        // persist change (will cascade change_items)
        Change saved = changeRepository.save(change);

        // insert chat_message SYSTEM (minimal body) referencing change
        String body = "Cambio aplicado";
        chatMessageGateway.insertSystemMessage(quoteId, "CHANGE_APPLIED", saved.getChangeId(), body, actorAccountId);

        // persist outbox snapshot (do NOT publish now)
        outboxAdapter.persistChangeOutbox(saved);

        return saved.getChangeId();
    }

    @Override
    @Transactional
    public Integer decideAndApplyChange(Integer quoteId, List<ChangeItem> items, Integer actorAccountId, Integer ifMatchVersion, String idempotencyKey) {
        Quote quote = quoteRepository.findById(quoteId).orElseThrow(() -> new DomainException("quote_not_found"));

        // If-Match optimistic check for direct-apply path
        if (ifMatchVersion != null && !ifMatchVersion.equals(quote.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(Quote.class, quoteId);
        }

        String state = quote.getStateCode();
        if ("PENDIENTE".equals(state)) {
            // only creator can perform direct changes
            if (!actorAccountId.equals(quote.getCreatedByAccountId())) {
                throw new DomainException("not_allowed_to_change_quote");
            }
            applyChangesToQuote(quote, items);
            quoteRepository.save(quote);
            return null;
        }

        if ("TRATO".equals(state) || "EN_ESPERA".equals(state)) {
            // require chat participant
            boolean isParticipant = chatParticipantGateway.exists(quoteId, actorAccountId);
            if (!isParticipant) throw new DomainException("not_chat_participant");
            // delegate to applyFreeChange which contains permission checks for requester/provider member
            return applyFreeChange(quoteId, items, actorAccountId, ifMatchVersion, idempotencyKey);
        }

        throw new DomainException("change_not_allowed_in_state");
    }

    private void applyChangesToQuote(Quote quote, List<ChangeItem> items) {
        for (ChangeItem ci : items) {
            switch (ci.getFieldCode()) {
                case "PRICE_TOTAL":
                    try {
                        BigDecimal newTotal = new BigDecimal(ci.getNewValue());
                        quote.updateTotalAmount(newTotal);
                    } catch (Exception ex) {
                        throw new DomainException("change_invalid_price_total");
                    }
                    break;
                case "QTY":
                    if (ci.getTargetQuoteItemId() == null) throw new DomainException("change_missing_target");
                    var found = quote.getItems().stream()
                            .filter(it -> it.getId().equals(ci.getTargetQuoteItemId()))
                            .findFirst()
                            .orElseThrow(() -> new DomainException("quote_item_not_found"));
                    try {
                        BigDecimal newQty = new BigDecimal(ci.getNewValue());
                        quote.updateItemQty(found.getRequestItemId(), newQty);
                    } catch (Exception ex) {
                        throw new DomainException("change_invalid_qty");
                    }
                    break;
                case "ITEM_ADD":
                    if (ci.getTargetRequestItemId() == null) throw new DomainException("change_missing_target");
                    try {
                        BigDecimal qty = ci.getNewValue() == null ? BigDecimal.ONE : new BigDecimal(ci.getNewValue());
                        quote.addItem(ci.getTargetRequestItemId(), qty);
                    } catch (Exception ex) {
                        throw new DomainException("change_invalid_item_add");
                    }
                    break;
                case "ITEM_REMOVE":
                    if (ci.getTargetQuoteItemId() == null) throw new DomainException("change_missing_target");
                    var f = quote.getItems().stream()
                            .filter(it -> it.getId().equals(ci.getTargetQuoteItemId()))
                            .findFirst()
                            .orElseThrow(() -> new DomainException("quote_item_not_found"));
                    quote.removeItem(f.getRequestItemId());
                    break;
                default:
                    throw new DomainException("change_item_field_unknown");
            }
        }
    }
}
