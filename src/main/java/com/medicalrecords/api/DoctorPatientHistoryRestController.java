package com.medicalrecords.api;

import com.medicalrecords.data.entity.Appointment;
import com.medicalrecords.data.entity.Patient;
import com.medicalrecords.data.repository.AppointmentRepository;
import com.medicalrecords.data.repository.InsuranceMonthRepository;
import com.medicalrecords.data.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/doctor/patients")
public class DoctorPatientHistoryRestController {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final InsuranceMonthRepository insuranceMonthRepository;

    @GetMapping("/{patientId}/history")
    public ResponseEntity<?> viewPatientHistory(@PathVariable("patientId") Long patientId,
                                                @AuthenticationPrincipal OidcUser authenticatedUser) {

        String doctorKeycloakId = authenticatedUser.getSubject();

        boolean doctorHasAccess =
                appointmentRepository.existsByDoctor_User_KeycloakIdAndPatient_Id(doctorKeycloakId, patientId);

        if (!doctorHasAccess) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You do not have access to this patient's history."));
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));

        List<Appointment> completedAppointments =
                appointmentRepository.findAllByPatient_IdAndCompletedTrueOrderByVisitDateDesc(patientId);

        return ResponseEntity.ok(
                Map.of(
                        "patient", patient,
                        "appointments", completedAppointments,
                        "insuranceMonths", insuranceMonthRepository.findAllByPatient_IdOrderByMonthValueDesc(patientId)
                )
        );
    }
}