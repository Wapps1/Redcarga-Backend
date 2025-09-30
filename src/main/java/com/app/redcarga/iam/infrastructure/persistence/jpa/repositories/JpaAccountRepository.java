package com.app.redcarga.iam.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.iam.domain.model.aggregates.Account;
import com.app.redcarga.iam.domain.repositories.AccountRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaAccountRepository extends JpaRepository<Account, Integer>, AccountRepository {

    Optional<Account> findByEmailIgnoreCase(String email);

    Optional<Account> findByExternalUid(String externalUid);
}
