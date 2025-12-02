package com.app.redcarga.media.infrastructure.cloudinary;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CloudinaryUploadAdapter {

    private final CloudinaryConfig config;
    private final RestTemplate restTemplate = new RestTemplate();

    public UploadResult upload(byte[] fileBytes, String folder, String resourceType) {
        try {
            // 1. Preparar parámetros firmados
            long timestamp = System.currentTimeMillis() / 1000L;
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("timestamp", timestamp);
            params.put("folder", folder);
            
            // 2. Generar firma SHA-1
            String signature = generateSignature(params);
            
            // 3. Construir form data
            MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
            form.add("file", new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() {
                    return "upload";
                }
            });
            form.add("api_key", config.getApiKey());
            form.add("timestamp", timestamp);
            form.add("folder", folder);
            form.add("signature", signature);
            
            // 4. Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            // 5. URL de Cloudinary
            String rt = (resourceType == null || resourceType.isBlank()) ? "image" : resourceType;
            String url = String.format("https://api.cloudinary.com/v1_1/%s/%s/upload", 
                config.getCloudName(), rt);
            
            // 6. POST request
            ResponseEntity<Map> response = restTemplate.postForEntity(
                url, 
                new HttpEntity<>(form, headers), 
                Map.class
            );
            
            Map<String, Object> result = response.getBody();
            return new UploadResult(
                (String) result.get("public_id"),
                (String) result.get("secure_url")
            );
            
        } catch (HttpStatusCodeException e) {
            throw e; // MediaExceptionHandler captura esto
        } catch (Exception e) {
            throw new RuntimeException("cloudinary_upload_failed", e);
        }
    }

    private String generateSignature(Map<String, Object> params) {
        try {
            // Construir string to sign: key1=value1&key2=value2&api_secret
            String toSign = params.entrySet().stream()
                .filter(e -> e.getValue() != null && !e.getValue().toString().isBlank())
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
            
            toSign = toSign + config.getApiSecret();
            
            // SHA-1 hash
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(toSign.getBytes(StandardCharsets.UTF_8));
            
            // Convertir a hex
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
            
        } catch (Exception e) {
            throw new IllegalStateException("cloudinary_sign_error", e);
        }
    }

    public record UploadResult(String publicId, String secureUrl) {}
}
