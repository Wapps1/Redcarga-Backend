package com.app.redcarga.iam.domain.repositories;

import com.app.redcarga.iam.domain.model.aggregates.Session;
import com.app.redcarga.iam.domain.model.valueobjects.Platform;
import com.app.redcarga.iam.domain.model.valueobjects.SessionStatus;

import java.util.List;
import java.util.Optional;

public interface SessionRepository {
    Session save(Session session);

    Optional<Session> findById(Integer id);

    List<Session> findByAccountIdAndStatus(Integer accountId, SessionStatus status);

    Optional<Session> findActiveByAccountIdAndPlatform(Integer accountId, Platform platform);
}
