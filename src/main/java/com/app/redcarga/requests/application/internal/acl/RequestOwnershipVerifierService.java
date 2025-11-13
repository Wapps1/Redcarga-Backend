package com.app.redcarga.requests.application.internal.acl;

import com.app.redcarga.requests.domain.services.RequestQueryService;
import com.app.redcarga.shared.ws.auth.RequestOwnershipVerifierPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestOwnershipVerifierService implements RequestOwnershipVerifierPort {

    private final RequestQueryService requestQueryService;

    @Override
    public boolean isRequester(Integer requestId, Integer accountId) {
        return requestQueryService.isRequester(requestId, accountId);
    }
}
