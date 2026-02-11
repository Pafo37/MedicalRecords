package com.medicalrecords.controller;

import com.medicalrecords.data.repository.SickLeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/patient/sick-leaves")
@RequiredArgsConstructor
public class PatientSickLeaveController {

    private final SickLeaveRepository sickLeaveRepository;

    @GetMapping
    public String list(@AuthenticationPrincipal OidcUser authenticatedUser, Model model) {
        String patientKeycloakId = authenticatedUser.getSubject();

        model.addAttribute("sickLeaves",
                sickLeaveRepository.findAllByAppointment_Patient_User_KeycloakIdOrderByStartDateDesc(patientKeycloakId));

        return "patient_sick_leaves";
    }
}
