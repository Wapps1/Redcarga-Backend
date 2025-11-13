package com.app.redcarga.deals.interfaces.rest.requests;

import jakarta.validation.constraints.NotNull;

public record MarkChatReadRequest(@NotNull Integer lastSeenMessageId) {}
