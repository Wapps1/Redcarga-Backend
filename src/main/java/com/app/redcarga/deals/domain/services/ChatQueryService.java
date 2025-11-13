package com.app.redcarga.deals.domain.services;

import com.app.redcarga.deals.interfaces.rest.responses.ChatMessageDto;
import java.util.List;

public interface ChatQueryService {
    List<ChatMessageDto> getMessages(Integer quoteId, Integer afterId, Integer limit, Integer actorAccountId);
}
