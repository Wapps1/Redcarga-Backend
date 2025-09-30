package com.app.redcarga.iam.domain.repositories;

import com.app.redcarga.iam.domain.model.aggregates.Account;

import java.util.Optional;

public interface AccountRepository {
    Account save(Account account);

    Optional<Account> findById(Integer id);
    Optional<Account> findByExternalUid(String externalUid);
    Optional<Account> findByEmail(String email);
    Optional<Account> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
