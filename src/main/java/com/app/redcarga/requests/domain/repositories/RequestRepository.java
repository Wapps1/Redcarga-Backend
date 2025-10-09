package com.app.redcarga.requests.domain.repositories;

import com.app.redcarga.requests.domain.model.aggregates.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository {
    Request save(Request request);
    Optional<Request> findById(Integer id);
    boolean existsById(Integer id);
    List<Request> findAllByRequesterAccountId(Integer requesterAccountId);
}
