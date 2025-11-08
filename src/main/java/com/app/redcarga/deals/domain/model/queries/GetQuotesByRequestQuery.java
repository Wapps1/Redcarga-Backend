package com.app.redcarga.deals.domain.model.queries;

public record GetQuotesByRequestQuery(
    Integer requestId,
    String stateCode
) {}
