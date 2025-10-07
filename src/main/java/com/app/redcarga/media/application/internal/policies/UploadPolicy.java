package com.app.redcarga.media.application.internal.policies;

import com.app.redcarga.media.domain.model.valueobjects.SubjectRef;

public interface UploadPolicy {
    UploadSpec buildSpec(SubjectRef ref);

    // uploadPreset ahora es opcional (puede ser null si no usas presets)
    record UploadSpec(String folder, String uploadPreset, String resourceType) {
        public UploadSpec {
            if (folder == null || folder.isBlank()) throw new IllegalArgumentException("folder_required");
            if (resourceType == null || resourceType.isBlank()) resourceType = "image";
            // uploadPreset puede ser null o vacío → no se enviará
        }
    }
}
