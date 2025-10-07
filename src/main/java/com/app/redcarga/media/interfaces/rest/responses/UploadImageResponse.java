package com.app.redcarga.media.interfaces.rest.responses;

public record UploadImageResponse(
    String publicId,
    String secureUrl
) {}
