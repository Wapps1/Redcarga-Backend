package com.app.redcarga.iam.domain.model.queries;

public record GetBootstrapQuery(Integer accountId) {
    public GetBootstrapQuery {
        if (accountId == null || accountId <= 0) throw new IllegalArgumentException("invalid accountId");
    }
}