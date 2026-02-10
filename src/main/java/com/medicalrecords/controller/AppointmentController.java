package com.medicalrecords.controller;

import com.medicalrecords.data.dto.AppointmentDTO;
import com.medicalrecords.data.repository.DoctorRepository;
import com.medicalrecords.service.appointment.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/patient/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorRepository doctorRepository;

    @GetMapping("/create")
    public String showCreate(Model model) {
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("dto", AppointmentDTO.builder()
                .visitDate(LocalDateTime.now().withSecond(0).withNano(0))
                .build());

        return "appointment_create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("dto") AppointmentDTO dto,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal OidcUser oidcUser,
                         Model model) {

        model.addAttribute("doctors", doctorRepository.findAll());

        if (bindingResult.hasErrors()) {
            return "appointment_create";
        }

        try {
            appointmentService.createAppointment(dto, oidcUser.getSubject());
            return "redirect:/";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "appointment_create";
        }
    }
}