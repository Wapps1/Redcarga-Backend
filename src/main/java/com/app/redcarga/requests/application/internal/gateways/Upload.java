// Upload.java  (tipo neutro, sin dependencias de Spring)
package com.app.redcarga.requests.application.internal.gateways;

import java.util.Objects;

public record Upload(String filename, byte[] bytes) {
    public Upload {
        Objects.requireNonNull(filename, "filename");
        Objects.requireNonNull(bytes, "bytes");
    }
    public static Upload of(String filename, byte[] bytes) {
        return new Upload(filename != null ? filename : "upload.jpg", bytes);
    }
}
