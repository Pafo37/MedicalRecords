package com.medicalrecords.service;

import com.medicalrecords.service.keycloak.KeycloakServiceImpl;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeycloakServiceImplTest {

    @Mock
    private Keycloak keycloak;

    @Mock
    private RealmResource realmResource;

    @Mock
    private UsersResource usersResource;

    @Mock
    private UserResource userResource;

    @Mock
    private RolesResource rolesResource;

    @Mock
    private RoleResource roleResource;

    @Mock
    private org.keycloak.admin.client.resource.RoleMappingResource roleMappingResource;

    @Mock
    private org.keycloak.admin.client.resource.RoleScopeResource roleScopeResource;

    @Mock
    private Response response;

    @Captor
    private ArgumentCaptor<UserRepresentation> userRepresentationCaptor;

    @Captor
    private ArgumentCaptor<CredentialRepresentation> credentialRepresentationCaptor;

    @Captor
    private ArgumentCaptor<List<RoleRepresentation>> roleListCaptor;

    @InjectMocks
    private KeycloakServiceImpl keycloakService;

    @Test
    void registerUser_shouldThrowRuntimeException_whenUserCreationStatusIsNotCreated() {
        String username = "john";
        String password = "pass";
        String email = "john@example.com";
        String firstName = "John";
        String lastName = "Smith";
        String roleName = "ROLE_DOCTOR";

        when(keycloak.realm("medical-records-app")).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(400);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> keycloakService.registerUser(username, password, email, firstName, lastName, roleName)
        );

        assertEquals("Failed to create user: 400", exception.getMessage());

        verify(usersResource).create(userRepresentationCaptor.capture());
        UserRepresentation createdUserRepresentation = userRepresentationCaptor.getValue();
        assertEquals(username, createdUserRepresentation.getUsername());
        assertTrue(createdUserRepresentation.isEnabled());
        assertEquals(email, createdUserRepresentation.getEmail());
        assertEquals(firstName, createdUserRepresentation.getFirstName());
        assertEquals(lastName, createdUserRepresentation.getLastName());

        verify(realmResource, never()).roles();
        verify(usersResource, never()).get(anyString());
    }

    @Test
    void registerUser_shouldCreateUserResetPasswordAndAssignRole_whenCreationIsSuccessful() {
        String username = "john";
        String password = "pass";
        String email = "john@example.com";
        String firstName = "John";
        String lastName = "Smith";
        String roleName = "ROLE_DOCTOR";

        URI locationHeader = URI.create("http://localhost/admin/realms/medical-records-app/users/abc123");

        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(roleName);

        when(keycloak.realm("medical-records-app")).thenReturn(realmResource);

        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(201);
        when(response.getLocation()).thenReturn(locationHeader);

        when(usersResource.get("abc123")).thenReturn(userResource);

        when(realmResource.roles()).thenReturn(rolesResource);
        when(rolesResource.get(roleName)).thenReturn(roleResource);
        when(roleResource.toRepresentation()).thenReturn(roleRepresentation);

        when(userResource.roles()).thenReturn(roleMappingResource);
        when(roleMappingResource.realmLevel()).thenReturn(roleScopeResource);

        String createdUserId = keycloakService.registerUser(username, password, email, firstName, lastName, roleName);

        assertEquals("abc123", createdUserId);

        verify(usersResource).create(userRepresentationCaptor.capture());
        UserRepresentation createdUserRepresentation = userRepresentationCaptor.getValue();
        assertEquals(username, createdUserRepresentation.getUsername());
        assertTrue(createdUserRepresentation.isEnabled());
        assertEquals(email, createdUserRepresentation.getEmail());
        assertEquals(firstName, createdUserRepresentation.getFirstName());
        assertEquals(lastName, createdUserRepresentation.getLastName());

        verify(userResource).resetPassword(credentialRepresentationCaptor.capture());
        CredentialRepresentation capturedCredentialRepresentation = credentialRepresentationCaptor.getValue();
        assertEquals(CredentialRepresentation.PASSWORD, capturedCredentialRepresentation.getType());
        assertEquals(password, capturedCredentialRepresentation.getValue());
        assertFalse(capturedCredentialRepresentation.isTemporary());

        verify(roleScopeResource).add(roleListCaptor.capture());
        List<RoleRepresentation> assignedRoles = roleListCaptor.getValue();
        assertEquals(1, assignedRoles.size());
        assertEquals(roleName, assignedRoles.get(0).getName());
    }
}