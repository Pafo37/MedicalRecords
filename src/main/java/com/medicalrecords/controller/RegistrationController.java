package com.medicalrecords.controller;


import com.medicalrecords.data.dto.RegistrationDTO;
import com.medicalrecords.data.entity.User;
import com.medicalrecords.service.doctor.DoctorService;
import com.medicalrecords.service.keycloak.KeycloakService;
import com.medicalrecords.service.patient.PatientService;
import com.medicalrecords.service.user.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
public class RegistrationController {

    private final KeycloakService keycloakService;
    private final UserService userService;
    private final PatientService patientService;
    private final DoctorService doctorService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationDTO", new RegistrationDTO());
        model.addAttribute("roles", List.of("ROLE_PATIENT", "ROLE_DOCTOR"));
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registrationDTO") RegistrationDTO dto,
            BindingResult result,
            Model model
    ) {

        if (result.hasErrors()) {
            model.addAttribute("roles", List.of("ROLE_PATIENT", "ROLE_DOCTOR"));
            return "register";
        }

        String role = dto.getRole();

        if ("ROLE_PATIENT".equals(role)) {
            if (dto.getEgn() == null || dto.getEgn().isBlank()) {
                model.addAttribute("errorMessage", "EGN is required for patients.");
                return "register";
            }
        } else if ("ROLE_DOCTOR".equals(role)) {
            if (dto.getMedicalId() == null || dto.getMedicalId().isBlank()) {
                model.addAttribute("errorMessage", "Medical ID is required for doctors.");
                return "register";
            }
            if (dto.getSpecialty() == null || dto.getSpecialty().isBlank()) {
                model.addAttribute("errorMessage", "Specialty is required for doctors.");
                return "register";
            }
        } else {
            model.addAttribute("errorMessage", "Invalid role selected.");
            return "register";
        }

        try {
            String keycloakUserId = keycloakService.registerUser(
                    dto.getUsername(),
                    dto.getPassword(),
                    dto.getEmail(),
                    dto.getFirstName(),
                    dto.getLastName(),
                    dto.getRole()
            );

            User user = userService.createFromRegistration(dto, keycloakUserId);

            switch (dto.getRole()) {
                case "ROLE_PATIENT" -> patientService.createFromRegistration(dto, user);
                case "ROLE_DOCTOR" -> doctorService.createFromRegistration(dto, user);
                default -> {
                    model.addAttribute("roles", List.of("ROLE_PATIENT", "ROLE_DOCTOR"));
                    model.addAttribute("errorMessage", "Invalid role selected.");
                    return "register";
                }
            }

            return "redirect:/oauth2/authorization/keycloak";

        } catch (Exception e) {
            model.addAttribute("roles", List.of("ROLE_PATIENT", "ROLE_DOCTOR"));
            model.addAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "register";
        }
    }
}