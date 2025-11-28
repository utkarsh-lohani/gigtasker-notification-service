package com.gigtasker.notificationservice.config;

import org.gigtasker.common.security.GigTaskerSecurity;
import org.gigtasker.common.security.KeycloakRoleConverter;
import org.gigtasker.common.security.SecurityCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

@Configuration
@Import(GigTaskerSecurity.class)
public class SecurityConfig {

    @Bean
    public SecurityCustomizer notificationSecurityCustomizer() {
        return authorize -> authorize.requestMatchers("/ws/**").permitAll();
    }

    @Bean
    @Primary
    public JwtAuthenticationConverter notificationJwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        converter.setPrincipalClaimName("email");
        return converter;
    }
}