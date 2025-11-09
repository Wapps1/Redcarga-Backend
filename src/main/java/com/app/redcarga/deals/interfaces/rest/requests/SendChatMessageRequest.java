package com.app.redcarga.deals.interfaces.rest.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendChatMessageRequest(
        @Size(max = 40) String dedupKey,
        @NotBlank String kind,
        @Size(max = 4000) String text,
        @Size(max = 1000) String url,
        @Size(max = 4000) String caption
) {}
