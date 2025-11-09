package com.app.redcarga.requests.application.internal.acl;

import com.app.redcarga.requests.domain.services.RequestQueryService;
import com.app.redcarga.requests.interfaces.acl.RequestFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RequestFacadeImpl implements RequestFacade {

    private final RequestQueryService requestQueryService;

    @Override
    public boolean isRequester(Integer requestId, Integer accountId) {
        if (requestId == null || accountId == null) return false;
        return requestQueryService.isRequester(requestId, accountId);
    }

}