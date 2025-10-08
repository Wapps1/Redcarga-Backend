package com.app.redcarga.identity.application.internal.queryservices;

import com.app.redcarga.identity.domain.model.aggregates.Person;
import com.app.redcarga.identity.domain.repositories.PersonRepository;
import com.app.redcarga.identity.domain.services.PersonQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonQueryServiceImpl implements PersonQueryService {

    private final PersonRepository personRepository;

    @Override
    public Optional<Person> findByAccountId(Integer accountId) {
        return personRepository.findByAccountId(accountId);
    }
}