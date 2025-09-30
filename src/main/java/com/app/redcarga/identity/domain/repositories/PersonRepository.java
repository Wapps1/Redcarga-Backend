package com.app.redcarga.identity.domain.repositories;

import com.app.redcarga.identity.domain.model.aggregates.Person;

import java.util.Optional;

public interface PersonRepository {
    Person save(Person person);
    Optional<Person> findById(Integer id);
    Optional<Person> findByAccountId(Integer accountId);
    boolean existsByDocTypeIdAndDocNumber(Integer docTypeId, String docNumber);
    Optional<Person> findByDocTypeIdAndDocNumber(Integer docTypeId, String docNumber);
}
