package com.medicalrecords.api;


import com.medicalrecords.data.dto.RegistrationDTO;
import com.medicalrecords.data.entity.User;
import com.medicalrecords.service.doctor.DoctorService;
import com.medicalrecords.service.keycloak.KeycloakService;
import com.medicalrecords.service.patient.PatientService;
import com.medicalrecords.service.user.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/register")
public class RegistrationRestController {

    private final KeycloakService keycloakService;
    private final UserService userService;
    private final PatientService patientService;
    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<?> getRegistrationData() {
        return ResponseEntity.ok(
                Map.of(
                        "registrationDTO", new RegistrationDTO(),
                        "roles", List.of("ROLE_PATIENT", "ROLE_DOCTOR")
                )
        );
    }

    @PostMapping
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationDTO registrationDTO) {

        String role = registrationDTO.getRole();

        if ("ROLE_PATIENT".equals(role)) {
            if (registrationDTO.getEgn() == null || registrationDTO.getEgn().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("errorMessage", "EGN is required for patients."));
            }
        } else if ("ROLE_DOCTOR".equals(role)) {
            if (registrationDTO.getMedicalId() == null || registrationDTO.getMedicalId().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("errorMessage", "Medical ID is required for doctors."));
            }
            if (registrationDTO.getSpecialty() == null || registrationDTO.getSpecialty().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("errorMessage", "Specialty is required for doctors."));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("errorMessage", "Invalid role selected."));
        }

        try {
            String keycloakUserId = keycloakService.registerUser(
                    registrationDTO.getUsername(),
                    registrationDTO.getPassword(),
                    registrationDTO.getEmail(),
                    registrationDTO.getFirstName(),
                    registrationDTO.getLastName(),
                    registrationDTO.getRole()
            );

            User user = userService.createFromRegistration(registrationDTO, keycloakUserId);

            switch (registrationDTO.getRole()) {
                case "ROLE_PATIENT" -> patientService.createFromRegistration(registrationDTO, user);
                case "ROLE_DOCTOR" -> doctorService.createFromRegistration(registrationDTO, user);
                default -> {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("errorMessage", "Invalid role selected."));
                }
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of(
                            "message", "Registration successful.",
                            "keycloakUserId", keycloakUserId
                    )
            );

        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("errorMessage", "Registration failed: " + exception.getMessage())
            );
        }
    }
}