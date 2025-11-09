package com.app.redcarga.shared.infrastructure.acl;

import com.app.redcarga.requests.interfaces.acl.RequestFacade;
import com.app.redcarga.shared.ws.auth.RequestOwnershipVerifierPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Adapter that implements the shared RequestOwnershipVerifierPort using the Requests BC facade. */
@Component
@RequiredArgsConstructor
public class RequestsAclLocalAdapter implements RequestOwnershipVerifierPort {

    private final RequestFacade requestFacade;

    @Override
    public boolean isRequester(Integer requestId, Integer accountId) {
        return requestFacade.isRequester(requestId, accountId);
    }
}
