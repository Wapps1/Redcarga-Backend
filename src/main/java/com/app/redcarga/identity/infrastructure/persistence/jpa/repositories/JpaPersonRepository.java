package com.app.redcarga.identity.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.identity.domain.model.aggregates.Person;
import com.app.redcarga.identity.domain.repositories.PersonRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaPersonRepository
        extends JpaRepository<Person, Integer>, PersonRepository {

    // Spring Data implementa los de PersonRepository automáticamente si firmas coinciden
    Optional<Person> findByAccountId(Integer accountId);
    boolean existsByDocTypeIdAndDocNumber(Integer docTypeId, String docNumber);
    Optional<Person> findByDocTypeIdAndDocNumber(Integer docTypeId, String docNumber);
}
