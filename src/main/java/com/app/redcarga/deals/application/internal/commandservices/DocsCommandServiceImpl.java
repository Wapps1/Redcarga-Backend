package com.app.redcarga.deals.application.internal.commandservices;

import com.app.redcarga.deals.application.internal.gateways.ChatMessageGateway;
import com.app.redcarga.deals.domain.services.ChecklistItemCommandService;
import com.app.redcarga.deals.domain.services.DocsCommandService;
import com.app.redcarga.deals.infrastructure.outbound.DealsChatOutboxAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocsCommandServiceImpl implements DocsCommandService {

    private final ChecklistItemCommandService checklistItemCommandService;
    private final ChatMessageGateway chatMessageGateway;
    private final DealsChatOutboxAdapter chatOutboxAdapter;

    @Override
    @Transactional
    public void markDocGreRemitente(Integer quoteId, Integer actorAccountId) {
        // mark checklist item DONE (check dependencies)
        checklistItemCommandService.markItemDone(quoteId, "DOC_GRE_REMITENTE", true, actorAccountId);

        // persist system message and snapshot outbox for WS push
        int messageId = chatMessageGateway.insertSystemMessage(
                quoteId,
                "DOC_GRE_REMITENTE",
                null,
                null,
                "Documento remitente recibido",
                actorAccountId
        );
        var dto = chatMessageGateway.findAfter(quoteId, messageId - 1, 1).stream().findFirst().orElse(null);
        if (dto != null) chatOutboxAdapter.persistSystemMessageOutbox(dto);
    }

    @Override
    @Transactional
    public void markDocGreTransportista(Integer quoteId, Integer actorAccountId) {
        checklistItemCommandService.markItemDone(quoteId, "DOC_GRE_TRANSPORTISTA", true, actorAccountId);
        int messageId = chatMessageGateway.insertSystemMessage(
                quoteId,
                "DOC_GRE_TRANSPORTISTA",
                null,
                null,
                "Documento transportista recibido",
                actorAccountId
        );
        var dto = chatMessageGateway.findAfter(quoteId, messageId - 1, 1).stream().findFirst().orElse(null);
        if (dto != null) chatOutboxAdapter.persistSystemMessageOutbox(dto);
    }
}
