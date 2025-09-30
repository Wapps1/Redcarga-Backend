package com.app.redcarga.identity.domain.repositories;

import com.app.redcarga.identity.domain.model.entities.VerificationAttempt;

import java.util.List;

public interface VerificationAttemptRepository {
    VerificationAttempt save(VerificationAttempt attempt);
    List<VerificationAttempt> findByVerificationId(Integer verificationId);
}
