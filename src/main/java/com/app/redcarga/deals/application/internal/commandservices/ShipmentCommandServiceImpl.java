package com.app.redcarga.deals.application.internal.commandservices;

import com.app.redcarga.deals.application.internal.gateways.ChatMessageGateway;
import com.app.redcarga.deals.domain.services.ChecklistItemCommandService;
import com.app.redcarga.deals.domain.services.ShipmentCommandService;
import com.app.redcarga.deals.infrastructure.outbound.DealsChatOutboxAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShipmentCommandServiceImpl implements ShipmentCommandService {

    private final ChecklistItemCommandService checklistItemCommandService;
    private final ChatMessageGateway chatMessageGateway;
    private final DealsChatOutboxAdapter chatOutboxAdapter;

    @Override
    @Transactional
    public void markShipmentReceived(Integer quoteId, Integer actorAccountId) {
        checklistItemCommandService.markItemDone(quoteId, "SHIPMENT_RECEIVED", true, actorAccountId);

        int messageId = chatMessageGateway.insertSystemMessage(
                quoteId,
                "SHIPMENT_RECEIVED",
                null,
                null,
                "Envío recibido",
                actorAccountId
        );
        var dto = chatMessageGateway.findAfter(quoteId, messageId - 1, 1).stream().findFirst().orElse(null);
        if (dto != null) chatOutboxAdapter.persistSystemMessageOutbox(dto);
    }

    @Override
    @Transactional
    public void markShipmentSent(Integer quoteId, Integer actorAccountId) {
        checklistItemCommandService.markItemDone(quoteId, "SHIPMENT_SENT", true, actorAccountId);

        int messageId = chatMessageGateway.insertSystemMessage(
                quoteId,
                "SHIPMENT_SENT",
                null,
                null,
                "Envío enviado",
                actorAccountId
        );
        var dto = chatMessageGateway.findAfter(quoteId, messageId - 1, 1).stream().findFirst().orElse(null);
        if (dto != null) chatOutboxAdapter.persistSystemMessageOutbox(dto);
    }
}