package com.app.redcarga.identity.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.identity.domain.model.entities.VerificationAttempt;
import com.app.redcarga.identity.domain.repositories.VerificationAttemptRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaVerificationAttemptRepository
        extends JpaRepository<VerificationAttempt, Integer>, VerificationAttemptRepository {

    List<VerificationAttempt> findByVerificationId(Integer verificationId);
}