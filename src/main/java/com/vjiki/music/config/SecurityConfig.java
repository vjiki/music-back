package com.vjiki.music.config;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.google.firebase.auth.FirebaseAuth;
import com.vjiki.music.repository.PlaylistRepository;
import com.vjiki.music.repository.UserRepository;
import com.vjiki.music.repository.UserRoleRepository;
import com.vjiki.music.security.FirebaseIdTokenFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${security.require-https:false}")
    private boolean requireHttps;

    @Value("${security.permit-all:false}")
    private boolean permitAll;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow all origins for development and production
        // In production, you might want to restrict this to specific domains
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));
        // Note: allowCredentials cannot be true with wildcard origins
        // If you need credentials, specify exact origins instead of "*"
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ObjectProvider<FirebaseIdTokenFilter> firebaseIdTokenFilterProvider) throws Exception {
        // Enable HTTPS enforcement behind a proxy that sets X-Forwarded-Proto=https.
        // Controlled by env var REQUIRE_HTTPS (see application.properties).
        // For local dev, keep it off.
        // Note: requires server.forward-headers-strategy=framework.
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .requiresChannel(channels -> {
                    if (requireHttps) {
                        channels.anyRequest().requiresSecure();
                    }
                })
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/healthz", "/actuator/**").permitAll()
                            .requestMatchers("/api/v1/auth/**").permitAll()
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    if (permitAll) {
                        auth.anyRequest().permitAll();
                    } else {
                        // Admin API
                        auth.requestMatchers("/api/v1/admin/**").hasRole("ADMIN");
                        auth.anyRequest().authenticated();
                    }
                });

        if (requireHttps) {
            http.headers(headers -> headers.httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .preload(true)
                    .maxAgeInSeconds(31536000)));
        }

        if (!permitAll) {
            FirebaseIdTokenFilter filter = firebaseIdTokenFilterProvider.getIfAvailable();
            if (filter != null) {
                http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
            }
        }

        return http.build();
    }

    @Bean
    @ConditionalOnProperty(prefix = "firebase", name = "enabled", havingValue = "true")
    public FirebaseIdTokenFilter firebaseIdTokenFilter(
            @Lazy FirebaseAuth firebaseAuth,
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            PlaylistRepository playlistRepository) {
        return new FirebaseIdTokenFilter(firebaseAuth, userRepository, userRoleRepository, playlistRepository);
    }
}
