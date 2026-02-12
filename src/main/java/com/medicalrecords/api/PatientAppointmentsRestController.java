package com.medicalrecords.api;


import com.medicalrecords.data.entity.Appointment;
import com.medicalrecords.data.entity.SickLeave;
import com.medicalrecords.data.repository.AppointmentRepository;
import com.medicalrecords.data.repository.SickLeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/patient/appointments")
public class PatientAppointmentsRestController {

    private final AppointmentRepository appointmentRepository;
    private final SickLeaveRepository sickLeaveRepository;

    @GetMapping
    public List<Appointment> listPatientAppointments(@AuthenticationPrincipal OidcUser authenticatedUser) {

        String patientKeycloakId = authenticatedUser.getSubject();

        return appointmentRepository
                .findAllByPatient_User_KeycloakIdOrderByVisitDateAsc(patientKeycloakId);
    }

    @GetMapping("/{appointmentId}")
    public Map<String, Object> viewPatientAppointment(@PathVariable("appointmentId") Long appointmentId,
                                                      @AuthenticationPrincipal OidcUser authenticatedUser) {

        String patientKeycloakId = authenticatedUser.getSubject();

        Appointment appointment = appointmentRepository
                .findByIdAndPatient_User_KeycloakId(appointmentId, patientKeycloakId)
                .orElseThrow(() -> new SecurityException("Appointment not found."));

        Optional<SickLeave> sickLeaveOptional = sickLeaveRepository.findByAppointment_Id(appointmentId);

        return Map.of(
                "appointment", appointment,
                "sickLeave", sickLeaveOptional.orElse(null)
        );
    }
}