package com.app.redcarga.requests.domain.repositories;

import com.app.redcarga.requests.domain.model.aggregates.Request;

import java.util.Optional;

public interface RequestRepository {
    Request save(Request request);
    Optional<Request> findById(Integer id);
    boolean existsById(Integer id);
}
