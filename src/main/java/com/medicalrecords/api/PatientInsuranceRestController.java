package com.medicalrecords.api;

import com.medicalrecords.data.entity.InsuranceMonth;
import com.medicalrecords.data.entity.Patient;
import com.medicalrecords.data.entity.User;
import com.medicalrecords.data.repository.PatientRepository;
import com.medicalrecords.data.repository.UserRepository;
import com.medicalrecords.service.insurance.InsuranceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/patient/insurance")
public class PatientInsuranceRestController {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final InsuranceService insuranceService;

    @GetMapping
    public ResponseEntity<?> getInsuranceInformation(@AuthenticationPrincipal OidcUser authenticatedUser) {

        String keycloakId = authenticatedUser.getSubject();

        User localUser = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Patient patient = patientRepository.findByUser(localUser)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        List<InsuranceMonth> lastSixMonths =
                insuranceService.getLastSixMonthsForPatient(patient);

        return ResponseEntity.ok(
                Map.of(
                        "patient", patient,
                        "lastSixMonths", lastSixMonths
                )
        );
    }

    @PostMapping("/pay")
    public ResponseEntity<?> payInsuranceMonth(@AuthenticationPrincipal OidcUser authenticatedUser,
                                               @RequestParam("month") String month) {

        String keycloakId = authenticatedUser.getSubject();

        User localUser = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Patient patient = patientRepository.findByUser(localUser)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        try {
            YearMonth monthToPay = YearMonth.parse(month); // expected format: 2026-02
            insuranceService.payMonth(patient, monthToPay);

            return ResponseEntity.ok(Map.of("message", "Insurance month paid."));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", exception.getMessage()));
        }
    }
}