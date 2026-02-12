package com.medicalrecords.api;

import com.medicalrecords.data.entity.Appointment;
import com.medicalrecords.data.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/patient/diagnoses")
public class PatientDiagnosisHistoryRestController {

    private final AppointmentRepository appointmentRepository;

    @GetMapping
    public List<Appointment> getPatientDiagnosisHistory(@AuthenticationPrincipal OidcUser authenticatedUser) {

        String patientKeycloakId = authenticatedUser.getSubject();

        return appointmentRepository
                .findAllByPatient_User_KeycloakIdAndCompletedTrueAndDiagnosisIsNotNullOrderByVisitDateDesc(patientKeycloakId);
    }
}