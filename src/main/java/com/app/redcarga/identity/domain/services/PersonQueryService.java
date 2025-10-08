package com.app.redcarga.identity.domain.services;

import com.app.redcarga.identity.domain.model.aggregates.Person;
import java.util.Optional;

public interface PersonQueryService {
    Optional<Person> findByAccountId(Integer accountId);
}