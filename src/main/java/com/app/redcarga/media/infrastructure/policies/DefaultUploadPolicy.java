package com.app.redcarga.media.infrastructure.policies;

import com.app.redcarga.media.application.internal.policies.UploadPolicy;
import com.app.redcarga.media.domain.model.valueobjects.MediaSubjectType;
import com.app.redcarga.media.domain.model.valueobjects.SubjectRef;
import org.springframework.stereotype.Component;

@Component
public class DefaultUploadPolicy implements UploadPolicy {

    @Override
    public UploadSpec buildSpec(SubjectRef ref) {
        switch (ref.type()) {
            case REQUEST_ITEM_PHOTO -> {
                String folder = "requests/" + ref.key() + "/";
                return new UploadSpec(folder, null, "image"); // ← sin preset
            }
            case PROVIDER_LOGO -> {
                String folder = "providers/" + ref.key() + "/logo/";
                return new UploadSpec(folder, null, "image"); // ← sin preset
            }
            default -> throw new IllegalArgumentException("unsupported_subject_type");
        }
    }
}
