package com.medicalrecords.api;

import com.medicalrecords.data.dto.AppointmentCompleteDTO;
import com.medicalrecords.data.dto.AppointmentDoctorNotesDTO;
import com.medicalrecords.data.dto.PrescriptionDTO;
import com.medicalrecords.data.dto.SickLeaveDTO;
import com.medicalrecords.data.entity.Appointment;
import com.medicalrecords.data.entity.SickLeave;
import com.medicalrecords.data.repository.PrescriptionRepository;
import com.medicalrecords.data.repository.SickLeaveRepository;
import com.medicalrecords.service.appointment.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/doctor/appointments")
public class DoctorAppointmentsRestController {

    private final AppointmentService appointmentService;
    private final PrescriptionRepository prescriptionRepository;
    private final SickLeaveRepository sickLeaveRepository;

    @GetMapping
    public ResponseEntity<?> listDoctorAppointments(@AuthenticationPrincipal OidcUser authenticatedUser) {

        String doctorKeycloakId = authenticatedUser.getSubject();

        return ResponseEntity.ok(
                appointmentService.getAppointmentsForDoctor(doctorKeycloakId)
        );
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<?> viewDoctorAppointment(@PathVariable("appointmentId") Long appointmentId,
                                                   @AuthenticationPrincipal OidcUser authenticatedUser) {

        String doctorKeycloakId = authenticatedUser.getSubject();

        Appointment appointment = appointmentService.getAppointmentForDoctor(appointmentId, doctorKeycloakId);

        LocalDate today = LocalDate.now();
        LocalDate appointmentDay = appointment.getVisitDate().toLocalDate();

        boolean isScheduledForToday = appointmentDay.equals(today);
        boolean isCompleted = appointment.isCompleted();
        boolean canComplete = isScheduledForToday && !isCompleted;

        SickLeave existingSickLeave = sickLeaveRepository
                .findByAppointment_Id(appointmentId)
                .orElse(null);

        return ResponseEntity.ok(
                Map.of(
                        "appointment", appointment,
                        "canComplete", canComplete,
                        "isScheduledForToday", isScheduledForToday,
                        "isCompleted", isCompleted,
                        "existingPrescriptions", prescriptionRepository.findAllByAppointment_IdOrderByIdDesc(appointmentId),
                        "existingSickLeave", existingSickLeave
                )
        );
    }

    @PostMapping("/{appointmentId}/notes")
    public ResponseEntity<?> saveDoctorNotes(@PathVariable("appointmentId") Long appointmentId,
                                             @Valid @RequestBody AppointmentDoctorNotesDTO doctorNotesRequest,
                                             @AuthenticationPrincipal OidcUser authenticatedUser) {

        appointmentService.updateDoctorNotes(
                appointmentId,
                authenticatedUser.getSubject(),
                doctorNotesRequest.getDoctorNotes()
        );

        return ResponseEntity.ok(Map.of("message", "Doctor notes saved."));
    }

    @PostMapping("/{appointmentId}/prescriptions")
    public ResponseEntity<?> addPrescription(@PathVariable("appointmentId") Long appointmentId,
                                             @Valid @RequestBody PrescriptionDTO prescriptionRequest,
                                             @AuthenticationPrincipal OidcUser authenticatedUser) {

        appointmentService.addPrescription(
                appointmentId,
                authenticatedUser.getSubject(),
                prescriptionRequest
        );

        return ResponseEntity.ok(Map.of("message", "Prescription added."));
    }

    @PostMapping("/{appointmentId}/sick-leave")
    public ResponseEntity<?> saveSickLeave(@PathVariable("appointmentId") Long appointmentId,
                                           @Valid @RequestBody SickLeaveDTO sickLeaveRequest,
                                           @AuthenticationPrincipal OidcUser authenticatedUser) {

        appointmentService.upsertSickLeave(
                appointmentId,
                authenticatedUser.getSubject(),
                sickLeaveRequest
        );

        return ResponseEntity.ok(Map.of("message", "Sick leave saved."));
    }

    @PostMapping("/{appointmentId}/complete")
    public ResponseEntity<?> completeAppointment(@PathVariable("appointmentId") Long appointmentId,
                                                 @Valid @RequestBody AppointmentCompleteDTO completeRequest,
                                                 @AuthenticationPrincipal OidcUser authenticatedUser) {
        try {
            appointmentService.completeAppointment(
                    appointmentId,
                    authenticatedUser.getSubject(),
                    completeRequest
            );
            return ResponseEntity.ok(Map.of("message", "Appointment completed."));
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
        }
    }
}