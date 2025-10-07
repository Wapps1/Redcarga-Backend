package com.app.redcarga.media.domain.model.valueobjects;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.util.Objects;

/** File payload for server-side upload. */
public final class UploadFile {
    private final String filename;
    private final String contentType;
    private final byte[] bytes;

    private UploadFile(String filename, String contentType, byte[] bytes) {
        this.filename = require(filename, "filename");
        this.contentType = require(contentType, "contentType");
        this.bytes = Objects.requireNonNull(bytes, "bytes");
        if (bytes.length == 0) throw new IllegalArgumentException("empty_file");
        if (bytes.length > 10 * 1024 * 1024) throw new IllegalArgumentException("file_too_large");
    }

    public static UploadFile of(String filename, String contentType, byte[] bytes) {
        return new UploadFile(filename, contentType, bytes);
    }

    public String filename() { return filename; }
    public String contentType() { return contentType; }
    public byte[] bytes() { return bytes; }

    public Resource asResource() {
        return new ByteArrayResource(bytes) {
            @Override public String getFilename() { return filename; }
            @Override public String getDescription() { return "UploadFile:" + filename; }
        };
    }

    private static String require(String v, String name) {
        if (v == null || (v = v.trim()).isEmpty()) throw new IllegalArgumentException(name + "_required");
        return v;
    }
}
