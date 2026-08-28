package com.vjiki.music.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

@Configuration
public class FirebaseConfig {

    /**
     * Enable by setting: FIREBASE_ENABLED=true
     *
     * Provide service account credentials using one of:
     * - FIREBASE_SERVICE_ACCOUNT_JSON: raw JSON string
     * - FIREBASE_SERVICE_ACCOUNT_JSON_BASE64: base64-encoded JSON string
     */
    @Bean
    @ConditionalOnProperty(prefix = "firebase", name = "enabled", havingValue = "true")
    public FirebaseAuth firebaseAuth() throws IOException {
        String json = trimToNull(System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON"));
        String jsonBase64 = trimToNull(System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON_BASE64"));

        byte[] bytes;
        if (json != null) {
            bytes = json.getBytes(StandardCharsets.UTF_8);
        } else if (jsonBase64 != null) {
            bytes = Base64.getDecoder().decode(jsonBase64);
        } else {
            throw new IllegalStateException(
                    "Firebase is enabled but FIREBASE_SERVICE_ACCOUNT_JSON(_BASE64) is not set");
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(bytes));
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        FirebaseApp app = FirebaseApp.getApps().stream()
                .findFirst()
                .orElseGet(() -> FirebaseApp.initializeApp(options));
        return FirebaseAuth.getInstance(app);
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
