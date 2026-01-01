package com.vjiki.music.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import java.util.Base64

@Configuration
class FirebaseConfig {

    /**
     * Enable by setting: FIREBASE_ENABLED=true
     *
     * Provide service account credentials using one of:
     * - FIREBASE_SERVICE_ACCOUNT_JSON: raw JSON string
     * - FIREBASE_SERVICE_ACCOUNT_JSON_BASE64: base64-encoded JSON string
     */
    @Bean
    @ConditionalOnProperty(prefix = "firebase", name = ["enabled"], havingValue = "true")
    fun firebaseAuth(): FirebaseAuth {
        val json = System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON")?.trim()
        val jsonBase64 = System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON_BASE64")?.trim()

        val bytes = when {
            !json.isNullOrBlank() -> json.toByteArray(StandardCharsets.UTF_8)
            !jsonBase64.isNullOrBlank() -> Base64.getDecoder().decode(jsonBase64)
            else -> throw IllegalStateException(
                "Firebase is enabled but FIREBASE_SERVICE_ACCOUNT_JSON(_BASE64) is not set"
            )
        }

        val credentials = GoogleCredentials.fromStream(ByteArrayInputStream(bytes))
        val options = FirebaseOptions.builder()
            .setCredentials(credentials)
            .build()

        val app = FirebaseApp.getApps().firstOrNull() ?: FirebaseApp.initializeApp(options)
        return FirebaseAuth.getInstance(app)
    }
}


