package com.app.redcarga.iam.domain.model.queries;

public record GetMeQuery(Integer accountId) {
    public GetMeQuery {
        if (accountId == null || accountId <= 0) throw new IllegalArgumentException("invalid accountId");
    }
}
