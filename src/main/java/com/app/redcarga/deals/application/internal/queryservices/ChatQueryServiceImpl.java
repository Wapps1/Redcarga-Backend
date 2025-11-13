package com.app.redcarga.deals.application.internal.queryservices;

import com.app.redcarga.deals.application.internal.gateways.ChatMessageGateway;
import com.app.redcarga.deals.application.internal.gateways.ChatParticipantGateway;
import com.app.redcarga.deals.domain.services.ChatQueryService;
import com.app.redcarga.shared.domain.exceptions.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatQueryServiceImpl implements ChatQueryService {

    private final ChatMessageGateway chatMessageGateway;
    private final ChatParticipantGateway chatParticipantGateway;

    @Override
    @Transactional(readOnly = true)
    public java.util.List<com.app.redcarga.deals.interfaces.rest.responses.ChatMessageDto> getMessages(Integer quoteId, Integer afterId, Integer limit, Integer actorAccountId) {
        if (!chatParticipantGateway.exists(quoteId, actorAccountId)) {
            throw new DomainException("not_chat_participant");
        }
        if (limit == null || limit < 1 || limit > 200) throw new DomainException("limit_invalid");
        return chatMessageGateway.findAfter(quoteId, afterId == null ? 0 : afterId, limit);
    }
}
