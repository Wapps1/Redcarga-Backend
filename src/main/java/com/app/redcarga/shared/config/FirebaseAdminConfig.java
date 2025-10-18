package com.app.redcarga.shared.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

@Configuration
public class FirebaseAdminConfig {

    @Value("${app.firebase.credentials-file}")
    private Resource credentialsFile;

    @Value("${app.firebase.project-id}")
    private String projectId;

    @Bean
    public FirebaseApp firebaseApp() throws Exception {
        InputStream in;

        String firebaseB64 = System.getenv("FIREBASE_ADMIN_B64");
        if (firebaseB64 != null && !firebaseB64.isEmpty()) {
            byte[] decoded = Base64.getDecoder().decode(firebaseB64);
            in = new ByteArrayInputStream(decoded);
        } else {
            in = credentialsFile.getInputStream();
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(in))
                .setProjectId(projectId)
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp app) {
        // Garantiza que se use el app inicializado arriba
        return FirebaseAuth.getInstance(app);
    }
}
