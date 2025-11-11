package com.app.redcarga.requests.application.internal.acl;

import com.app.redcarga.requests.domain.repositories.RequestAcceptanceRepository;
import com.app.redcarga.requests.domain.services.RequestCommandService;
import com.app.redcarga.requests.domain.services.RequestQueryService;
import com.app.redcarga.requests.interfaces.acl.RequestFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestFacadeImpl implements RequestFacade {

    private final RequestQueryService requestQueryService;
    private final RequestCommandService requestCommandService;
    private final RequestAcceptanceRepository requestAcceptanceRepository;

    @Override
    public boolean isRequester(Integer requestId, Integer accountId) {
        if (requestId == null || accountId == null) return false;
        return requestQueryService.isRequester(requestId, accountId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RequestAcceptanceSnapshot> getAcceptanceSnapshot(Integer requestId) {
        if (requestId == null) return Optional.empty();

        // Get status from the aggregate (if exists)
        var maybeReq = requestQueryService.findById(requestId);
        if (maybeReq.isEmpty()) return Optional.empty();
        Integer statusId = maybeReq.get().currentStatus().getId();

        // accepted quote stored in DB; use acceptance repo to read it
        Integer acceptedQuoteId = requestAcceptanceRepository.findAcceptedQuoteId(requestId).orElse(null);

        return Optional.of(new RequestAcceptanceSnapshot(requestId, acceptedQuoteId, statusId));
    }
}