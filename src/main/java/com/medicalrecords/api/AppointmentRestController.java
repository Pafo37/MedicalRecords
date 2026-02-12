package com.medicalrecords.api;

import com.medicalrecords.data.dto.AppointmentDTO;
import com.medicalrecords.data.repository.DoctorRepository;
import com.medicalrecords.service.appointment.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/patient/appointments")
public class AppointmentRestController {

    private final AppointmentService appointmentService;
    private final DoctorRepository doctorRepository;

    @GetMapping("/create")
    public ResponseEntity<?> getCreateAppointmentData() {

        AppointmentDTO defaultAppointmentDTO = AppointmentDTO.builder()
                .visitDate(LocalDateTime.now().withSecond(0).withNano(0))
                .build();

        return ResponseEntity.ok(
                Map.of(
                        "doctors", doctorRepository.findAll(),
                        "defaultAppointment", defaultAppointmentDTO
                )
        );
    }

    @PostMapping
    public ResponseEntity<?> createAppointment(@Valid @RequestBody AppointmentDTO appointmentDTO,
                                               @AuthenticationPrincipal OidcUser authenticatedUser) {

        try {
            appointmentService.createAppointment(appointmentDTO, authenticatedUser.getSubject());
            return ResponseEntity.ok(Map.of("message", "Appointment created."));
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
        }
    }
}