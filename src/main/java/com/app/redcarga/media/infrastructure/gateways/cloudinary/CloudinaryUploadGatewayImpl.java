package com.app.redcarga.media.infrastructure.gateways.cloudinary;

import com.app.redcarga.media.application.internal.gateways.CloudinaryUploadGateway;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class CloudinaryUploadGatewayImpl implements CloudinaryUploadGateway {

    private final RestTemplate rest = new RestTemplate();
    private final CloudinaryProperties props;

    public CloudinaryUploadGatewayImpl(CloudinaryProperties props) {
        this.props = props;
    }

    @Override
    public Map<String, Object> upload(String resourceType, Map<String, Object> signedParams, Resource fileResource) {
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", fileResource);
        form.add("api_key", props.getApiKey());
        signedParams.forEach((k,v) -> { if (v != null) form.add(k, v.toString()); });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        String rt = (resourceType == null || resourceType.isBlank()) ? "image" : resourceType;
        String url = String.format("https://api.cloudinary.com/v1_1/%s/%s/upload", props.getCloudName(), rt);

        try {
            ResponseEntity<Map> res = rest.postForEntity(url, new HttpEntity<>(form, headers), Map.class);
            return res.getBody();
        } catch (HttpStatusCodeException e) {
            throw e; // lo captura MediaExceptionHandler y devuelve el 4xx/5xx real de Cloudinary
        }
    }
}
