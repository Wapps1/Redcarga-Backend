package com.app.redcarga.identity.domain.repositories;

import com.app.redcarga.identity.domain.model.entities.KycVerification;

import java.util.Optional;

public interface KycVerificationRepository {
    KycVerification save(KycVerification verification);
    Optional<KycVerification> findById(Integer id);
    Optional<KycVerification> findBySessionId(String sessionId);
}
