package com.app.redcarga.requests.domain.services;

import java.util.Optional;

public interface RequestQueryService {
    Optional<Integer> existsById(Integer requestId);
}
