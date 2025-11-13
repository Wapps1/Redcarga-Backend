package com.app.redcarga.deals.domain.services;

import com.app.redcarga.deals.domain.model.commands.SendUserChatMessageCommand;

public interface ChatCommandService {
    Integer sendUserMessage(SendUserChatMessageCommand cmd);
    void markRead(Integer quoteId, Integer actorAccountId, Integer lastSeenMessageId);
}
