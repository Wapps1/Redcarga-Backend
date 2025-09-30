package com.app.redcarga.shared.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.InputStream;

@Configuration
public class FirebaseAdminConfig {

    @Value("${app.firebase.credentials-file}")
    private Resource credentialsFile;

    @Value("${app.firebase.project-id}")
    private String projectId;

    @Bean
    public FirebaseApp firebaseApp() throws Exception {
        try (InputStream in = credentialsFile.getInputStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(in))
                    .setProjectId(projectId)
                    .build();

            // Evita inicializar dos veces
            if (FirebaseApp.getApps().isEmpty()) {
                return FirebaseApp.initializeApp(options);
            }
            return FirebaseApp.getInstance();
        }
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp app) {
        // Garantiza que se use el app inicializado arriba
        return FirebaseAuth.getInstance(app);
    }
}
