package com.medicalrecords.service.keycloak;

import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@AllArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {
    private final Keycloak keycloak;
    private final String REALM = "medical-records-app";

    @Override
    public String registerUser(String username, String password, String email, String firstName, String lastName, String roleName) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEnabled(true);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        Response response = keycloak.realm(REALM).users().create(user);
        if (response.getStatus() != 201) {
            throw new RuntimeException("Failed to create user: " + response.getStatus());
        }
        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);
        keycloak.realm(REALM).users().get(userId).resetPassword(passwordCred);
        RoleRepresentation role = keycloak.realm(REALM).roles().get(roleName).toRepresentation();
        keycloak.realm(REALM).users().get(userId).roles().realmLevel().add(Collections.singletonList(role));
        return userId;
    }
}