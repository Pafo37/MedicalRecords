package com.medicalrecords.api;

import com.medicalrecords.data.entity.SickLeave;
import com.medicalrecords.data.repository.SickLeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/patient/sick-leaves")
public class PatientSickLeaveRestController {

    private final SickLeaveRepository sickLeaveRepository;

    @GetMapping
    public List<SickLeave> getPatientSickLeaves(@AuthenticationPrincipal OidcUser authenticatedUser) {

        String patientKeycloakId = authenticatedUser.getSubject();

        return sickLeaveRepository
                .findAllByAppointment_Patient_User_KeycloakIdOrderByStartDateDesc(patientKeycloakId);
    }
}