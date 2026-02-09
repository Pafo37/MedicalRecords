package com.medicalrecords.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Bean
    public Keycloak keycloakAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl("http://localhost:8081")
                .realm("medical-records-app")
                .clientId("admin-cli")
                .clientSecret("fSBMkkr2CDyHOSqEctBXTbyidr9aqHpy")
                .username("pafo")
                .password("123")
                .grantType("client_credentials")
                .build();
    }
}
