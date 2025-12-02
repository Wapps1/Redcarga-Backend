package com.app.redcarga.deals.application.internal.commandservices;

import com.app.redcarga.deals.application.internal.gateways.ChatMessageGateway;
import com.app.redcarga.deals.application.internal.gateways.ChatParticipantGateway;
import com.app.redcarga.deals.domain.model.entities.Change;
import com.app.redcarga.deals.domain.model.entities.ChangeItem;
import com.app.redcarga.deals.domain.model.aggregates.Quote;
import com.app.redcarga.deals.domain.repositories.ChangeItemRepository;
import com.app.redcarga.deals.domain.services.ChangeCommandService;
import com.app.redcarga.deals.infrastructure.persistence.jpa.repositories.JpaChangeRepository;
import com.app.redcarga.deals.infrastructure.outbound.DealsChangeOutboxAdapter;
import com.app.redcarga.deals.domain.repositories.QuoteRepository;
import com.app.redcarga.deals.application.internal.outboundservices.acl.ProvidersMembershipClient;
import com.app.redcarga.deals.application.internal.outboundservices.acl.RequestsServiceClient;
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
    private final RequestsServiceClient requestsClient;
    private final JpaChangeRepository changeRepository;
    private final ChatMessageGateway chatMessageGateway;
    private final DealsChangeOutboxAdapter outboxAdapter;
    private final ChatParticipantGateway chatParticipantGateway;
    private final ChangeItemRepository changeItemRepository;

    @Override
    @Transactional
    public Integer applyFreeChange(Integer quoteId, List<ChangeItem> items, Integer actorAccountId, Integer ifMatchVersion, String idempotencyKey) {
        // restore original applyFreeChange behavior: permission is requester or provider member, only TRATO/EN_ESPERA
        Quote quote = quoteRepository.findById(quoteId).orElseThrow(() -> new DomainException("quote_not_found"));

        // permission: either requester or member of provider company
        boolean isRequester = requestsClient.isRequester(quote.getRequestId(), actorAccountId);
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
    chatMessageGateway.insertSystemMessage(quoteId, "CHANGE_APPLIED", saved.getChangeId(), null, body, actorAccountId);

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

        // NUEVO: cuando la quote ya está ACEPTADA -> crear propuesta de cambio y dejarla PENDIENTE
        if ("ACEPTADA".equals(state)) {
            // require chat participant (mismo requisito que en TRATO/EN_ESPERA)
            boolean isParticipant = chatParticipantGateway.exists(quoteId, actorAccountId);
            if (!isParticipant) throw new DomainException("not_chat_participant");

            // crear Change de tipo PROPUESTA en estado PENDIENTE (usar factory existente y cambiar estado)
            Change change = Change.createApplied(quote, "PROPUESTA", actorAccountId); // factory set quote/kind/createdBy
            change.markPending(); // dejar PENDIENTE
            for (ChangeItem it : items) {
                change.addItem(it);
            }
            Change saved = changeRepository.save(change);

            // mensaje de sistema y snapshot outbox (tipo CHANGE_PROPOSED)
            String body = "Cambio propuesto";
            chatMessageGateway.insertSystemMessage(quoteId, "CHANGE_PROPOSED", saved.getChangeId(), null, body, actorAccountId);
            outboxAdapter.persistChangeOutbox(saved);

            return saved.getChangeId();
        }


        throw new DomainException("change_not_allowed_in_state");
    }

    /*Decidir si un cambio es aceptado o rechazado*/
    @Override
    @Transactional
    public Integer decideOnProposedChange(Integer changeId, boolean accept, Integer actorAccountId, Integer ifMatchVersion) {
        // Cargar change sin lazy collections problemáticas
        Change change = changeRepository.findById(changeId)
                .orElseThrow(() -> new DomainException("change_not_found"));

        if (!"PENDIENTE".equals(change.getStatusCode())) {
            throw new DomainException("change_not_pending");
        }

        // cannot decide own change
        if (actorAccountId.equals(change.getCreatedBy())) {
            throw new DomainException("cannot_decide_own_change");
        }

        // Cargar quote por separado
        Quote quote = quoteRepository.findById(change.getQuote().getId())
                .orElseThrow(() -> new DomainException("quote_not_found"));

        if (!"ACEPTADA".equals(quote.getStateCode())) {
            throw new DomainException("change_decision_not_allowed_in_state");
        }

        // optional optimistic check against quote version
        if (ifMatchVersion != null && !ifMatchVersion.equals(quote.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(Quote.class, quote.getId());
        }

        if (accept) {
            // CARGAR ITEMS POR SEPARADO usando el nuevo repositorio
            List<ChangeItem> items = changeItemRepository.findByChangeId(changeId);

            // apply items to quote and persist
            applyChangesToQuote(quote, items);
            quoteRepository.save(quote);

            // mark applied and persist change
            change.markApplied();
            Change saved = changeRepository.save(change);

            // chat message + outbox snapshot
            String body = "Cambio aceptado";
            chatMessageGateway.insertSystemMessage(quote.getId(), "CHANGE_ACCEPTED", saved.getChangeId(), null, body, actorAccountId);
            outboxAdapter.persistChangeOutbox(saved);

            return saved.getChangeId();
        } else {
            // reject
            change.markRejected();
            Change saved = changeRepository.save(change);

            String body = "Cambio rechazado";
            chatMessageGateway.insertSystemMessage(quote.getId(), "CHANGE_REJECTED", saved.getChangeId(), null, body, actorAccountId);
            outboxAdapter.persistChangeOutbox(saved);

            return saved.getChangeId();
        }
    }
    private void applyChangesToQuote(Quote quote, List<ChangeItem> items) {
        System.out.println("========== APLICANDO CAMBIOS ==========");
        System.out.println("Total de items a aplicar: " + items.size());
        
        for (ChangeItem ci : items) {
            System.out.println("--- ChangeItem ---");
            System.out.println("fieldCode: " + ci.getFieldCode());
            System.out.println("newValue: " + ci.getNewValue());
            System.out.println("oldValue: " + ci.getOldValue());
            System.out.println("targetQuoteItemId: " + ci.getTargetQuoteItemId());
            System.out.println("targetRequestItemId: " + ci.getTargetRequestItemId());
            
            switch (ci.getFieldCode()) {
                case "PRICE_TOTAL":
                    try {
                        System.out.println("Intentando parsear newValue como BigDecimal: " + ci.getNewValue());
                        BigDecimal newTotal = new BigDecimal(ci.getNewValue());
                        System.out.println("newTotal parseado correctamente: " + newTotal);
                        quote.updateTotalAmount(newTotal);
                        System.out.println("Total actualizado exitosamente");
                    } catch (Exception ex) {
                        System.err.println("ERROR al parsear PRICE_TOTAL: " + ex.getMessage());
                        ex.printStackTrace();
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
                        System.out.println("Intentando parsear QTY: " + ci.getNewValue());
                        BigDecimal newQty = new BigDecimal(ci.getNewValue());
                        System.out.println("newQty parseado: " + newQty);
                        quote.updateItemQty(found.getRequestItemId(), newQty);
                    } catch (Exception ex) {
                        System.err.println("ERROR al parsear QTY: " + ex.getMessage());
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
                    System.err.println("Campo desconocido: " + ci.getFieldCode());
                    throw new DomainException("change_item_field_unknown");
            }
        }
        System.out.println("========== FIN APLICAR CAMBIOS ==========");
    }
}
