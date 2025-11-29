package com.app.redcarga.media.interfaces.rest.responses;

public record UploadPdfResponse(
    String fileId,
    String cdnUrl
) {}
