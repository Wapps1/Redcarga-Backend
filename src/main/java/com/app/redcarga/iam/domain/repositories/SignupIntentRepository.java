package com.app.redcarga.iam.domain.repositories;
import com.app.redcarga.iam.domain.model.aggregates.SignupIntent;

import java.util.Optional;

public interface SignupIntentRepository {
    SignupIntent save(SignupIntent intent);

    Optional<SignupIntent> findById(Integer id);

    /** Intent “abierto” para una cuenta (PENDING/EMAIL_VERIFIED/BASIC_PROFILE_COMPLETED). */
    Optional<SignupIntent> findOpenByAccountId(Integer accountId);

    void flush();
}
