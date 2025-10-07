package com.app.redcarga.media.application.internal.gateways;

import java.util.Map;

public interface CloudinarySignerGateway {
    /** Firma SHA1 ordenada con api_secret. Devuelve la firma (hex). */
    String sign(Map<String, Object> params);
}
