package com.medicalrecords.service.keycloak;

public interface KeycloakService {

    String registerUser(String username,
                        String password,
                        String email,
                        String firstName,
                        String lastName,
                        String roleName);
}