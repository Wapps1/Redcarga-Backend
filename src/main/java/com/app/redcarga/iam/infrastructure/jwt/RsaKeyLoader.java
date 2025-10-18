package com.app.redcarga.iam.infrastructure.jwt;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Component
public class RsaKeyLoader {

    private final DefaultResourceLoader resourceLoader = new DefaultResourceLoader();

    public RSAPrivateKey loadPrivate(String location) {
        try {
            String pem;

            String envVal = System.getenv("IAM_PRIVATE_PEM_B64");
            if (envVal != null && !envVal.isEmpty()) {
                // Puede ser PEM directo o base64(PEM)
                if (envVal.contains("-----BEGIN")) {
                    pem = envVal;
                } else {
                    byte[] decoded = Base64.getDecoder().decode(envVal);
                    pem = new String(decoded, StandardCharsets.UTF_8);
                }
            } else {
                // Local: carga el recurso indicado por la propiedad (file:, classpath:, etc.)
                Resource res = resourceLoader.getResource(location);
                if (!res.exists()) {
                    throw new IllegalStateException("No existe recurso de private key: " + location);
                }
                try (var is = res.getInputStream()) {
                    pem = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                }
            }

            // soporta PKCS#8 PEM y limpia el contenido
            String cleaned = pem
                    .replaceAll("-----BEGIN ([A-Z ]+)-----", "")
                    .replaceAll("-----END ([A-Z ]+)-----", "")
                    .replaceAll("\\s", "");

            byte[] der = Base64.getDecoder().decode(cleaned);
            var spec = new PKCS8EncodedKeySpec(der);
            return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (Exception e) {
            throw new IllegalStateException("No pude leer la private key IAM desde: " + location, e);
        }
    }
}