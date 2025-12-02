package com.app.redcarga.deals.application.internal.gateways;

import com.app.redcarga.deals.interfaces.rest.responses.ChatMessageDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatMessageGateway {
    Optional<ChatMessageDto> findByDedupKey(int quoteId, UUID dedupKey);
    int insertUserMessage(int quoteId, String contentCode, String body, String mediaUrl, UUID dedupKey, int createdBy);
    int insertSystemMessage(int quoteId, String systemSubtypeCode, Integer refChangeId, Integer refAcceptanceId, String body, int createdBy);
    List<ChatMessageDto> findAfter(int quoteId, int afterId, int limit);
    int findMaxMessageId(int quoteId);
    
    // Get all messages for a quote (no pagination)
    List<ChatMessageDto> findAll(int quoteId);
    
    // Get last read message ID for a user in a quote (0 if never read)
    Integer getLastReadMessageId(int quoteId, int userId);

    // Count messages after a given message ID (for unread count)
    int countAfter(int quoteId, int afterId);
}
