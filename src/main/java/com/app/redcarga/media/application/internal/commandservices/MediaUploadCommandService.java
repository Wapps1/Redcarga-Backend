package com.app.redcarga.media.application.internal.commandservices;

import com.app.redcarga.media.application.internal.gateways.CloudinarySignerGateway;
import com.app.redcarga.media.application.internal.gateways.CloudinaryUploadGateway;
import com.app.redcarga.media.application.internal.gateways.SubjectAuthorizationPort;
import com.app.redcarga.media.application.internal.policies.UploadPolicy;
import com.app.redcarga.media.domain.model.valueobjects.SubjectRef;
import com.app.redcarga.media.domain.model.valueobjects.UploadFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class MediaUploadCommandService {

    private final UploadPolicy policy;
    private final CloudinarySignerGateway signer;
    private final CloudinaryUploadGateway uploader;
    private final SubjectAuthorizationPort authz;

    public MediaUploadCommandService(UploadPolicy policy,
                                     CloudinarySignerGateway signer,
                                     CloudinaryUploadGateway uploader,
                                     SubjectAuthorizationPort authz) {
        this.policy = policy;
        this.signer = signer;
        this.uploader = uploader;
        this.authz = authz;
    }

    @Transactional
    public Map<String, Object> upload(SubjectRef subject, UploadFile file) {
        authz.assertCanUpload(subject);

        var spec = policy.buildSpec(subject);
        long ts = System.currentTimeMillis() / 1000L;

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("timestamp", ts);
        params.put("folder", spec.folder());
        // upload_preset es opcional; no lo enviamos si es null/blank
        if (spec.uploadPreset() != null && !spec.uploadPreset().isBlank()) {
            params.put("upload_preset", spec.uploadPreset());
        }
        params.put("signature", signer.sign(params));

        Resource resource = new ByteArrayResource(file.bytes()) {
            @Override public String getFilename() { return file.filename(); }
        };

        return uploader.upload(spec.resourceType(), params, resource);
    }
}
