package com.app.redcarga.requests.application.internal.queryservices;

import com.app.redcarga.requests.domain.model.aggregates.Request;
import com.app.redcarga.requests.domain.repositories.RequestRepository;
import com.app.redcarga.requests.domain.services.RequestQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestQueryServiceImpl implements RequestQueryService {

    private final RequestRepository requests;

    @Override
    public Optional<Request> findById(Integer requestId) {
        return requests.findById(requestId);
    }

    @Override
    public Optional<Request> findDetailById(Integer requestId) {
        return requests.findByIdWithItemsAndImages(requestId);
    }

    @Override
    public boolean existsById(Integer requestId) {
        return requests.existsById(requestId);
    }

    @Override
    public List<Request> findAllByRequester(Integer requesterAccountId) {
        return requests.findAllByRequesterAccountId(requesterAccountId);
    }

    @Override
    public boolean isRequester(Integer requestId, Integer accountId) {
        if (requestId == null || accountId == null) return false;
        return requests.findById(requestId)
                .map(r -> accountId.equals(r.getRequesterAccountId()))
                .orElse(false);
    }
}
