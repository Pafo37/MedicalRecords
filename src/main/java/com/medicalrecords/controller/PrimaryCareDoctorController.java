package com.medicalrecords.controller;

import com.medicalrecords.data.entity.Doctor;
import com.medicalrecords.data.entity.Patient;
import com.medicalrecords.data.entity.User;
import com.medicalrecords.data.repository.DoctorRepository;
import com.medicalrecords.data.repository.PatientRepository;
import com.medicalrecords.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/primary-care-doctor")
public class PrimaryCareDoctorController {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @GetMapping("/choose")
    public String showSelectionPage(@AuthenticationPrincipal OidcUser oidcUser, Model model) {

        String keycloakId = oidcUser.getSubject();

        User localUser = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Patient patient = patientRepository.findByUser(localUser)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        model.addAttribute("patient", patient);
        model.addAttribute("doctors", doctorRepository.findAll());

        return "primary_care_doctor";
    }

    @PostMapping("/choose")
    public String assignPrimaryCareDoctor(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RequestParam("doctorId") Long doctorId) {

        String keycloakId = oidcUser.getSubject();

        User localUser = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Patient patient = patientRepository.findByUser(localUser)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Doctor selectedDoctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        patient.setPrimaryCareDoctor(selectedDoctor);
        patientRepository.save(patient);

        return "redirect:/";
    }
}