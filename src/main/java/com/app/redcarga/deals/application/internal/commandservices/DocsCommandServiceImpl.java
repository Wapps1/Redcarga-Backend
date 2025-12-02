package com.app.redcarga.deals.application.internal.commandservices;

import com.app.redcarga.deals.application.internal.gateways.ChatMessageGateway;
import com.app.redcarga.deals.domain.exceptions.GuideAlreadyExistsException;
import com.app.redcarga.deals.domain.exceptions.QuoteNotAcceptedException;
import com.app.redcarga.deals.domain.model.aggregates.Quote;
import com.app.redcarga.deals.domain.model.entities.Guide;
import com.app.redcarga.deals.domain.model.valueobjects.GuideType;
import com.app.redcarga.deals.domain.repositories.GuideRepository;
import com.app.redcarga.deals.domain.repositories.QuoteRepository;
import com.app.redcarga.deals.domain.services.ChecklistItemCommandService;
import com.app.redcarga.deals.domain.services.DocsCommandService;
import com.app.redcarga.deals.infrastructure.outbound.DealsChatOutboxAdapter;
import com.app.redcarga.shared.domain.exceptions.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocsCommandServiceImpl implements DocsCommandService {

    private final ChecklistItemCommandService checklistItemCommandService;
    private final ChatMessageGateway chatMessageGateway;
    private final DealsChatOutboxAdapter chatOutboxAdapter;
    private final GuideRepository guideRepository;
    private final QuoteRepository quoteRepository;

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

    @Override
    @Transactional
    public Guide createGuide(GuideType type, Integer quoteId, String guideUrl) {
        // Validate quote exists
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new DomainException("quote_not_found"));
        
        // Validate quote is ACEPTADA
        if (!"ACEPTADA".equals(quote.getStateCode())) {
            throw new QuoteNotAcceptedException(quoteId);
        }
        
        // Validate if guide already exists
        if (guideRepository.existsByQuoteIdAndType(quoteId, type)) {
            throw new GuideAlreadyExistsException(quoteId, type);
        }
        
        Guide guide = new Guide();
        guide.setType(type);
        guide.setQuoteId(quoteId);
        guide.setGuideUrl(guideUrl);
        return guideRepository.save(guide);
    }

    @Override
    @Transactional
    public Guide updateGuideUrl(Integer guideId, String guideUrl) {
        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new RuntimeException("Guide not found with id: " + guideId));
        guide.setGuideUrl(guideUrl);
        return guideRepository.save(guide);
    }
}
