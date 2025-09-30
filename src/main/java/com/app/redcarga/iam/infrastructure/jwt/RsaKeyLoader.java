package com.app.redcarga.iam.infrastructure.jwt;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Component
public class RsaKeyLoader {
    private final ResourceLoader resources;

    public RsaKeyLoader(ResourceLoader resources) { this.resources = resources; }

    public RSAPrivateKey loadPrivate(String location) {
        try {
            Resource res = resources.getResource(location); // p.ej. file:./.secrets/iam/dev/iam_private.pem
            String pem = Files.readString(res.getFile().toPath(), StandardCharsets.UTF_8);
            String base64 = pem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] der = Base64.getDecoder().decode(base64);
            var keySpec = new PKCS8EncodedKeySpec(der);
            return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        } catch (Exception e) {
            throw new IllegalStateException("No pude leer la private key: " + location, e);
        }
    }
}
