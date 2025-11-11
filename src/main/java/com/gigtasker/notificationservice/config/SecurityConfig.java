package com.gigtasker.notificationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * This bean defines the *HTTP* security rules.
     * It pokes a hole in the firewall for the /ws handshake.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .authorizeHttpRequests(authorize -> authorize

                        // 1. Allow the WebSocket handshake to get through
                        .requestMatchers("/ws/**").permitAll()

                        // 2. Allow any "preflight" OPTIONS requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 3. Secure everything else
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}));

        return http.build();
    }

    /**
     * This bean defines the *WebSocket* security mapping.
     * It tells Spring to use the "email" as the user's name,
     * not the "sub" (subject) ID.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        // This makes "sendToUser("user@example.com", ...)" work
        converter.setPrincipalClaimName("email");

        return converter;
    }
}