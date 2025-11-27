package com.app.redcarga.deals.domain.services;

import com.app.redcarga.deals.domain.model.entities.Change;
import com.app.redcarga.deals.interfaces.rest.responses.ChangeResponse;

import java.util.Optional;

public interface ChangeQueryService {
    Optional<ChangeResponse> getChangeWithItems(Integer changeId);
}