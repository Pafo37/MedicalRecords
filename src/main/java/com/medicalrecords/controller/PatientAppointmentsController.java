package com.medicalrecords.controller;

import com.medicalrecords.data.entity.Appointment;
import com.medicalrecords.data.repository.AppointmentRepository;
import com.medicalrecords.data.repository.SickLeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/patient/appointments")
@RequiredArgsConstructor
public class PatientAppointmentsController {

    private final AppointmentRepository appointmentRepository;
    private final SickLeaveRepository sickLeaveRepository;

    @GetMapping
    public String list(@AuthenticationPrincipal OidcUser authenticatedUser, Model model) {
        String patientKeycloakId = authenticatedUser.getSubject();
        model.addAttribute("appointments",
                appointmentRepository.findAllByPatient_User_KeycloakIdOrderByVisitDateAsc(patientKeycloakId));
        return "patient_appointments";
    }

    @GetMapping("/{appointmentId}")
    public String view(@PathVariable("appointmentId") Long appointmentId,
                       @AuthenticationPrincipal OidcUser authenticatedUser,
                       Model model) {

        String patientKeycloakId = authenticatedUser.getSubject();

        Appointment appointment = appointmentRepository
                .findByIdAndPatient_User_KeycloakId(appointmentId, patientKeycloakId)
                .orElseThrow(() -> new SecurityException("Appointment not found."));

        model.addAttribute("appointment", appointment);
        model.addAttribute("sickLeave", sickLeaveRepository.findByAppointment_Id(appointmentId).orElse(null));

        return "patient_appointment_view";
    }
}