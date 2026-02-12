package com.medicalrecords.api;

import com.medicalrecords.data.entity.Doctor;
import com.medicalrecords.data.entity.Patient;
import com.medicalrecords.data.entity.User;
import com.medicalrecords.data.repository.DoctorRepository;
import com.medicalrecords.data.repository.PatientRepository;
import com.medicalrecords.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/primary-care-doctor")
public class PrimaryCareDoctorRestController {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @GetMapping("/choose")
    public ResponseEntity<?> getPrimaryCareDoctorSelectionData(@AuthenticationPrincipal OidcUser authenticatedUser) {

        String keycloakId = authenticatedUser.getSubject();

        User localUser = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Patient patient = patientRepository.findByUser(localUser)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        List<Doctor> doctors = doctorRepository.findAll();

        return ResponseEntity.ok(
                Map.of(
                        "patient", patient,
                        "doctors", doctors
                )
        );
    }

    @PostMapping("/choose")
    public ResponseEntity<?> assignPrimaryCareDoctor(@AuthenticationPrincipal OidcUser authenticatedUser,
                                                     @RequestParam("doctorId") Long doctorId) {

        String keycloakId = authenticatedUser.getSubject();

        User localUser = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Patient patient = patientRepository.findByUser(localUser)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Doctor selectedDoctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        patient.setPrimaryCareDoctor(selectedDoctor);
        patientRepository.save(patient);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Primary care doctor assigned.",
                        "patientId", patient.getId(),
                        "doctorId", selectedDoctor.getId()
                )
        );
    }
}