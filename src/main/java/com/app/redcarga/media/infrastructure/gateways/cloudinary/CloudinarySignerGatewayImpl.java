package com.app.redcarga.media.infrastructure.gateways.cloudinary;

import com.app.redcarga.media.application.internal.gateways.CloudinarySignerGateway;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CloudinarySignerGatewayImpl implements CloudinarySignerGateway {

    private final CloudinaryProperties props;

    public CloudinarySignerGatewayImpl(CloudinaryProperties props) {
        this.props = props;
    }

    @Override
    public String sign(Map<String, Object> params) {
        String toSign = params.entrySet().stream()
                .filter(e -> e.getValue() != null && !e.getValue().toString().isBlank())
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
        toSign = toSign + props.getApiSecret();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(toSign.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("cloudinary_sign_error", e);
        }
    }
}
