package com.medicalrecords.controller;

import com.medicalrecords.data.entity.InsuranceMonth;
import com.medicalrecords.data.entity.Patient;
import com.medicalrecords.data.entity.User;
import com.medicalrecords.data.repository.PatientRepository;
import com.medicalrecords.data.repository.UserRepository;
import com.medicalrecords.service.insurance.InsuranceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.YearMonth;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/patient/insurance")
public class PatientInsuranceController {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final InsuranceService insuranceService;

    @GetMapping
    public String showInsurancePage(@AuthenticationPrincipal OidcUser oidcUser, Model model) {

        String keycloakId = oidcUser.getSubject();

        User localUser = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Patient patient = patientRepository.findByUser(localUser)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        List<InsuranceMonth> lastSixMonths = insuranceService.getLastSixMonthsForPatient(patient);

        model.addAttribute("patient", patient);
        model.addAttribute("lastSixMonths", lastSixMonths);
        return "patient_insurance";
    }

    @PostMapping("/pay")
    public String payInsuranceMonth(@AuthenticationPrincipal OidcUser oidcUser,
                                    @RequestParam("month") String month) {

        String keycloakId = oidcUser.getSubject();

        User localUser = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Patient patient = patientRepository.findByUser(localUser)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        YearMonth monthToPay = YearMonth.parse(month); // format: 2026-02
        insuranceService.payMonth(patient, monthToPay);

        return "redirect:/patient/insurance";
    }
}