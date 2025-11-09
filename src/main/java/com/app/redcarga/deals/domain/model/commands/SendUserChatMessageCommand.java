package com.app.redcarga.deals.domain.model.commands;

import java.util.UUID;

public record SendUserChatMessageCommand(
        Integer quoteId,
        Integer actorAccountId,
        UUID dedupKey,
        String kind,
        String text,
        String imageUrl,
        String caption
) {}
