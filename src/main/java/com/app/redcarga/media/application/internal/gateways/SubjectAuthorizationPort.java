package com.app.redcarga.media.application.internal.gateways;

import com.app.redcarga.media.domain.model.valueobjects.SubjectRef;

public interface SubjectAuthorizationPort {
    /** Lanza excepción si el usuario NO está autorizado a subir para ese subject. */
    void assertCanUpload(SubjectRef ref);
}
