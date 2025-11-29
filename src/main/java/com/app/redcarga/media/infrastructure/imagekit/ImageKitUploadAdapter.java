package com.app.redcarga.media.infrastructure.imagekit;

import com.app.redcarga.media.interfaces.rest.responses.UploadPdfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ImageKitUploadAdapter {

    private final ImageKitConfig config;
    private final RestTemplate restTemplate = new RestTemplate();

    public UploadPdfResponse upload(byte[] fileBytes, String filename) {
        try {
            String url = "https://upload.imagekit.io/api/v1/files/upload";

            // ImageKit usa Basic Auth con private_key:
            String auth = config.getPrivateKey() + ":";
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Authorization", "Basic " + encodedAuth);

            // ImageKit requiere el archivo en base64 O como multipart file
            // Usaremos multipart file como en Cloudinary
            MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
            form.add("file", new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() {
                    return filename != null ? filename : "document.pdf";
                }
            });
            form.add("fileName", filename != null ? filename : "document.pdf");
            form.add("folder", "/pdfs");
            form.add("useUniqueFileName", "true");

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(form, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

            Map<String, Object> result = response.getBody();
            if (result == null) {
                throw new RuntimeException("imagekit_no_response");
            }

            // ImageKit devuelve: fileId, url, name, etc
            String fileId = result.get("fileId") != null ? result.get("fileId").toString() : "";
            String fileUrl = result.get("url") != null ? result.get("url").toString() : "";

            return new UploadPdfResponse(fileId, fileUrl);

        } catch (HttpStatusCodeException e) {
            throw e; // MediaExceptionHandler captura esto
        } catch (Exception e) {
            throw new RuntimeException("imagekit_upload_failed: " + e.getMessage(), e);
        }
    }
}
