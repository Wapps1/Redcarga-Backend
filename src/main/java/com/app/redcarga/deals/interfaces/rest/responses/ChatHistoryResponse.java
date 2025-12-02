package com.app.redcarga.deals.interfaces.rest.responses;

import java.util.List;

public record ChatHistoryResponse(
        Integer lastReadMessageId,
        List<ChatMessageDto> messages
) {}
