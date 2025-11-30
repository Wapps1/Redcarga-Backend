package com.app.redcarga.deals.application.internal.outboundservices.acl;

import com.app.redcarga.requests.interfaces.acl.RequestFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Outbound ACL client: deals BC consumes requests BC facade.
 * Encapsula la comunicación con RequestFacade según los estándares de arquitectura.
 */
@Service
@RequiredArgsConstructor
public class RequestsServiceClient {

    private final RequestFacade requestFacade;

    /**
     * Verifica si el accountId es el requester de la request.
     */
    public boolean isRequester(Integer requestId, Integer accountId) {
        return requestFacade.isRequester(requestId, accountId);
    }

    /**
     * Obtiene el snapshot de aceptación (quote aceptada + estado).
     */
    public Optional<RequestAcceptanceSnapshot> getAcceptanceSnapshot(Integer requestId) {
        return requestFacade.getAcceptanceSnapshot(requestId)
                .map(snap -> new RequestAcceptanceSnapshot(
                        snap.requestId(),
                        snap.acceptedQuoteId(),
                        snap.statusId()
                ));
    }

    /**
     * Cierra una request desde deals (por ejemplo, al aceptar una quote).
     */
    public void closeRequest(Integer requestId) {
        requestFacade.closeRequest(requestId);
    }

    /**
     * DTO local para deals (desacoplado del facade externo).
     */
    public record RequestAcceptanceSnapshot(
            Integer requestId,
            Integer acceptedQuoteId,
            Integer statusId
    ) {}
}
