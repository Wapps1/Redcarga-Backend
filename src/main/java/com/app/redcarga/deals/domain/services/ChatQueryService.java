package com.app.redcarga.deals.domain.services;

import com.app.redcarga.deals.interfaces.rest.responses.ChatMessageDto;
import com.app.redcarga.deals.interfaces.rest.responses.ChatHistoryResponse;
import com.app.redcarga.deals.interfaces.rest.responses.ChatListResponse;
import java.util.List;

public interface ChatQueryService {
    List<ChatMessageDto> getMessages(Integer quoteId, Integer afterId, Integer limit, Integer actorAccountId);
    
    // Get full chat history with last read message ID
    ChatHistoryResponse getFullHistory(Integer quoteId, Integer actorAccountId);

    // Get list of chats for a user
    ChatListResponse listChats(Integer userId, boolean isProvider);
}
