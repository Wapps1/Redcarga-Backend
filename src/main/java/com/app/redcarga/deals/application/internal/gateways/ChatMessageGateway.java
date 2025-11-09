package com.app.redcarga.deals.application.internal.gateways;

import com.app.redcarga.deals.interfaces.rest.responses.ChatMessageDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatMessageGateway {
    Optional<ChatMessageDto> findByDedupKey(int quoteId, UUID dedupKey);
    int insertUserMessage(int quoteId, String contentCode, String body, String mediaUrl, UUID dedupKey, int createdBy);
    int insertSystemMessage(int quoteId, String systemSubtypeCode, Integer refChangeId, String body, int createdBy);
    List<ChatMessageDto> findAfter(int quoteId, int afterId, int limit);
    int findMaxMessageId(int quoteId);
}
