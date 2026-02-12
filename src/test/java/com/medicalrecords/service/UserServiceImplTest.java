package com.medicalrecords.service;

import com.medicalrecords.data.dto.RegistrationDTO;
import com.medicalrecords.data.entity.User;
import com.medicalrecords.data.repository.UserRepository;
import com.medicalrecords.service.user.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void createFromRegistration_shouldSaveUserWithAllFieldsMappedCorrectly() {

        RegistrationDTO registrationDTO = mock(RegistrationDTO.class);
        String keycloakUserId = "kc-123";

        when(registrationDTO.getUsername()).thenReturn("john.doe");
        when(registrationDTO.getEmail()).thenReturn("john@example.com");
        when(registrationDTO.getFirstName()).thenReturn("John");
        when(registrationDTO.getLastName()).thenReturn("Doe");
        when(registrationDTO.getRole()).thenReturn("ROLE_DOCTOR");

        User savedUser = new User();
        savedUser.setId(50L);
        savedUser.setKeycloakId(keycloakUserId);
        savedUser.setUsername("john.doe");
        savedUser.setEmail("john@example.com");
        savedUser.setFirstName("John");
        savedUser.setLastName("Doe");
        savedUser.setRole("ROLE_DOCTOR");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.createFromRegistration(registrationDTO, keycloakUserId);

        assertNotNull(result);
        assertEquals(50L, result.getId());
        assertEquals(keycloakUserId, result.getKeycloakId());
        assertEquals("john.doe", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("ROLE_DOCTOR", result.getRole());

        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertEquals(keycloakUserId, capturedUser.getKeycloakId());
        assertEquals("john.doe", capturedUser.getUsername());
        assertEquals("john@example.com", capturedUser.getEmail());
        assertEquals("John", capturedUser.getFirstName());
        assertEquals("Doe", capturedUser.getLastName());
        assertEquals("ROLE_DOCTOR", capturedUser.getRole());
    }
}