package com.medicalrecords.controller;


import com.medicalrecords.data.dto.RegistrationDTO;
import com.medicalrecords.service.keycloak.KeycloakService;
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

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationDTO", new RegistrationDTO());
        model.addAttribute("roles", List.of("PATIENT", "DOCTOR"));
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registrationDTO") RegistrationDTO dto,
                           BindingResult result,
                           Model model) {

        if (result.hasErrors()) {
            model.addAttribute("roles", List.of("PATIENT", "DOCTOR"));
            return "register";
        }

        try {
            keycloakService.registerUser(
                    dto.getUsername(),
                    dto.getPassword(),
                    dto.getEmail(),
                    dto.getFirstName(),
                    dto.getLastName(),
                    dto.getRole()
            );

            return "redirect:/oauth2/authorization/keycloak";

        } catch (Exception e) {
            model.addAttribute("roles", List.of("PATIENT", "DOCTOR"));
            model.addAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "register";
        }
    }
}
