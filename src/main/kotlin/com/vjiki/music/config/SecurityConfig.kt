package com.vjiki.music.config

import com.google.firebase.auth.FirebaseAuth
import com.vjiki.music.repository.PlaylistRepository
import com.vjiki.music.repository.UserRepository
import com.vjiki.music.repository.UserRoleRepository
import com.vjiki.music.security.FirebaseIdTokenFilter
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.beans.factory.annotation.Value

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        // Allow all origins for development and production
        // In production, you might want to restrict this to specific domains
        configuration.allowedOriginPatterns = listOf("*")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD")
        configuration.allowedHeaders = listOf("*")
        configuration.exposedHeaders = listOf("*")
        // Note: allowCredentials cannot be true with wildcard origins
        // If you need credentials, specify exact origins instead of "*"
        configuration.allowCredentials = false
        configuration.maxAge = 3600L
        
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        firebaseIdTokenFilterProvider: ObjectProvider<FirebaseIdTokenFilter>
    ): SecurityFilterChain {
        // Enable HTTPS enforcement behind a proxy that sets X-Forwarded-Proto=https.
        // Controlled by env var REQUIRE_HTTPS (see application.properties).
        // For local dev, keep it off.
        // Note: requires server.forward-headers-strategy=framework.
        val requireHttps = requireHttps
        val permitAll = permitAll

        http
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .requiresChannel { channels ->
                if (requireHttps) {
                    channels.anyRequest().requiresSecure()
                }
            }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/healthz", "/actuator/**").permitAll()
                    .requestMatchers("/api/v1/auth/**").permitAll()
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .apply {
                        if (permitAll) {
                            anyRequest().permitAll()
                        } else {
                            // Admin API
                            requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                            anyRequest().authenticated()
                        }
                    }
            }

        if (requireHttps) {
            http.headers { headers ->
                headers.httpStrictTransportSecurity { hsts ->
                    hsts.includeSubDomains(true).preload(true).maxAgeInSeconds(31536000)
                }
            }
        }

        if (!permitAll) {
            firebaseIdTokenFilterProvider.ifAvailable?.let { filter ->
                http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter::class.java)
            }
        }

        return http.build()
    }

    @Value("\${security.require-https:false}")
    private var requireHttps: Boolean = false

    @Value("\${security.permit-all:false}")
    private var permitAll: Boolean = false

    @Bean
    @ConditionalOnProperty(prefix = "firebase", name = ["enabled"], havingValue = "true")
    fun firebaseIdTokenFilter(
        @Lazy firebaseAuth: FirebaseAuth,
        userRepository: UserRepository,
        userRoleRepository: UserRoleRepository,
        playlistRepository: PlaylistRepository
    ): FirebaseIdTokenFilter {
        return FirebaseIdTokenFilter(
            firebaseAuth = firebaseAuth,
            userRepository = userRepository,
            userRoleRepository = userRoleRepository,
            playlistRepository = playlistRepository
        )
    }
}

