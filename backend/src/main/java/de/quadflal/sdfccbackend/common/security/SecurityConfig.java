package de.quadflal.sdfccbackend.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
                }))
                // ── SECURITY HEADERS ────────────────────────────────────────────────
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                        .xssProtection(HeadersConfigurer.XXssConfig::disable)
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'; frame-ancestors 'none'"))
                        .referrerPolicy(referrer -> referrer
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000)))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints from OpenAPI
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/restaurants").permitAll()
                        .requestMatchers(HttpMethod.GET, "/restaurants/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/lists").permitAll()
                        .requestMatchers(HttpMethod.GET, "/lists/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/lists/*/restaurants").permitAll()

                        // Secured endpoints from OpenAPI
                        .requestMatchers(HttpMethod.POST, "/restaurants").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/restaurants/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/restaurants/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/lists").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/lists/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/lists/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/lists/*/restaurants").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/lists/*/restaurants/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/users/me").authenticated()

                        // Operational endpoints
                        .requestMatchers("/actuator/**").permitAll()

                        .anyRequest().denyAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ── CORS CONFIGURATION ────────────────────────────────────────────────────
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("https://yourfrontend.com"));
        config.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
