package com.app.redcarga.identity.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.identity.domain.model.entities.KycVerification;
import com.app.redcarga.identity.domain.repositories.KycVerificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaKycVerificationRepository
        extends JpaRepository<KycVerification, Integer>, KycVerificationRepository {

    Optional<KycVerification> findBySessionId(String sessionId);
}
