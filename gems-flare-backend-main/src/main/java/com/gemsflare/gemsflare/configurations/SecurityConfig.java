package com.gemsflare.gemsflare.configurations;

import com.gemsflare.gemsflare.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> {
                            try {
                                csrf.disable()
                                        .cors(cors -> cors.configurationSource(request -> {
                                            var config = new org.springframework.web.cors.CorsConfiguration();
                                            config.setAllowedOrigins(List.of(
                                                    "http://localhost:8080",
                                                    "http://localhost:8081",
                                                    "https://api.gemsflare.com",
                                                    "https://www.gemsflare.com",
                                                    "https://gemsflare.com",
                                                    "https://preview--product-paradise-35.lovable.app/"));
                                            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                                            config.setAllowedHeaders(List.of("*"));
                                            config.setAllowCredentials(true);
                                            return config;
                                        }));
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().permitAll()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}