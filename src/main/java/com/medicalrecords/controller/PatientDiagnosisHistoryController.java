package com.medicalrecords.controller;

import com.medicalrecords.data.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/patient/diagnoses")
@RequiredArgsConstructor
public class PatientDiagnosisHistoryController {

    private final AppointmentRepository appointmentRepository;

    @GetMapping
    public String list(@AuthenticationPrincipal OidcUser authenticatedUser, Model model) {
        String patientKeycloakId = authenticatedUser.getSubject();

        model.addAttribute("appointments",
                appointmentRepository.findAllByPatient_User_KeycloakIdAndCompletedTrueAndDiagnosisIsNotNullOrderByVisitDateDesc(patientKeycloakId));

        return "patient_diagnosis_history";
    }
}