package com.app.redcarga.deals.domain.services;

public interface DocsCommandService {
    void markDocGreRemitente(Integer quoteId, Integer actorAccountId);
    void markDocGreTransportista(Integer quoteId, Integer actorAccountId);
}
