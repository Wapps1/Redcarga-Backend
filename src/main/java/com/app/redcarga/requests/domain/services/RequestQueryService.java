package com.app.redcarga.requests.domain.services;

import com.app.redcarga.requests.domain.model.aggregates.Request;

import java.util.List;
import java.util.Optional;

public interface RequestQueryService {
    Optional<Request> findById(Integer requestId);
    boolean existsById(Integer requestId);
    List<Request> findAllByRequester(Integer requesterAccountId);
}
