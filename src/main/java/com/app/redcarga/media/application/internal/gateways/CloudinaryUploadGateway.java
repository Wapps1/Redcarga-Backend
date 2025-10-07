package com.app.redcarga.media.application.internal.gateways;

import org.springframework.core.io.Resource;
import java.util.Map;

public interface CloudinaryUploadGateway {
    Map<String, Object> upload(String resourceType, Map<String, Object> signedParams, Resource fileResource);
}
