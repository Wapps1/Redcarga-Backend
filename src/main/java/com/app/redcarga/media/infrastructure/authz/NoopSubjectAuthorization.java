package com.app.redcarga.media.infrastructure.authz;

import com.app.redcarga.media.application.internal.gateways.SubjectAuthorizationPort;
import com.app.redcarga.media.domain.model.valueobjects.SubjectRef;
import org.springframework.stereotype.Component;

@Component
public class NoopSubjectAuthorization implements SubjectAuthorizationPort {
    @Override
    public void assertCanUpload(SubjectRef ref) {
        // TODO: reemplazar por verificación real de ownership/membership según el subject.
        // Por ahora, no-op para dev.
    }
}
