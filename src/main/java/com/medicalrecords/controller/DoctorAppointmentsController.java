package com.medicalrecords.controller;

import com.medicalrecords.data.dto.AppointmentCompleteDTO;
import com.medicalrecords.data.dto.AppointmentDoctorNotesDTO;
import com.medicalrecords.data.dto.PrescriptionDTO;
import com.medicalrecords.data.dto.SickLeaveDTO;
import com.medicalrecords.data.entity.Appointment;
import com.medicalrecords.data.repository.PrescriptionRepository;
import com.medicalrecords.data.repository.SickLeaveRepository;
import com.medicalrecords.service.appointment.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/doctor/appointments")
@RequiredArgsConstructor
public class DoctorAppointmentsController {

    private final AppointmentService appointmentService;
    private final PrescriptionRepository prescriptionRepository;
    private final SickLeaveRepository sickLeaveRepository;

    @GetMapping
    public String showDoctorAppointments(@AuthenticationPrincipal OidcUser authenticatedUser, Model model) {
        String doctorKeycloakId = authenticatedUser.getSubject();
        model.addAttribute("appointments", appointmentService.getAppointmentsForDoctor(doctorKeycloakId));
        return "doctor_appointments";
    }

    @GetMapping("/{appointmentId}")
    public String showAppointmentDetails(@PathVariable("appointmentId") Long appointmentId,
                                         @AuthenticationPrincipal OidcUser authenticatedUser,
                                         Model model) {

        String doctorKeycloakId = authenticatedUser.getSubject();
        Appointment appointment = appointmentService.getAppointmentForDoctor(appointmentId, doctorKeycloakId);

        LocalDate today = LocalDate.now();
        LocalDate appointmentDay = appointment.getVisitDate().toLocalDate();

        boolean isScheduledForToday = appointmentDay.equals(today);
        boolean isCompleted = appointment.isCompleted();
        boolean canComplete = isScheduledForToday && !isCompleted;

        model.addAttribute("appointment", appointment);
        model.addAttribute("canComplete", canComplete);
        model.addAttribute("isScheduledForToday", isScheduledForToday);
        model.addAttribute("isCompleted", isCompleted);


        model.addAttribute("doctorNotesForm", AppointmentDoctorNotesDTO.builder()
                .doctorNotes(appointment.getDoctorNotes() == null ? "" : appointment.getDoctorNotes())
                .build());

        model.addAttribute("prescriptionForm", PrescriptionDTO.builder().build());
        model.addAttribute("existingPrescriptions",
                prescriptionRepository.findAllByAppointment_IdOrderByIdDesc(appointmentId));

        model.addAttribute("existingSickLeave",
                sickLeaveRepository.findByAppointment_Id(appointmentId).orElse(null));

        model.addAttribute("sickLeaveForm", SickLeaveDTO.builder().build());

        model.addAttribute("completeForm", AppointmentCompleteDTO.builder()
                .doctorNotes(appointment.getDoctorNotes() == null ? "" : appointment.getDoctorNotes())
                .diagnosis(appointment.getDiagnosis() == null ? "" : appointment.getDiagnosis().getName())
                .build());

        return "doctor_appointment_view";
    }

    @PostMapping("/{appointmentId}/notes")
    public String saveDoctorNotes(@PathVariable("appointmentId") Long appointmentId,
                                  @Valid @ModelAttribute("doctorNotesForm") AppointmentDoctorNotesDTO doctorNotesForm,
                                  BindingResult bindingResult,
                                  @AuthenticationPrincipal OidcUser authenticatedUser,
                                  Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Doctor notes are required.");
            return "redirect:/doctor/appointments/" + appointmentId;
        }

        appointmentService.updateDoctorNotes(appointmentId, authenticatedUser.getSubject(), doctorNotesForm.getDoctorNotes());
        return "redirect:/doctor/appointments/" + appointmentId;
    }

    @PostMapping("/{appointmentId}/prescriptions")
    public String addPrescription(@PathVariable("appointmentId") Long appointmentId,
                                  @Valid @ModelAttribute("prescriptionForm") PrescriptionDTO prescriptionForm,
                                  BindingResult bindingResult,
                                  @AuthenticationPrincipal OidcUser authenticatedUser) {

        if (bindingResult.hasErrors()) {
            return "redirect:/doctor/appointments/" + appointmentId;
        }

        appointmentService.addPrescription(appointmentId, authenticatedUser.getSubject(), prescriptionForm);
        return "redirect:/doctor/appointments/" + appointmentId;
    }

    @PostMapping("/{appointmentId}/sick-leave")
    public String saveSickLeave(@PathVariable("appointmentId") Long appointmentId,
                                @Valid @ModelAttribute("sickLeaveForm") SickLeaveDTO sickLeaveForm,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal OidcUser authenticatedUser) {

        if (bindingResult.hasErrors()) {
            return "redirect:/doctor/appointments/" + appointmentId;
        }

        appointmentService.upsertSickLeave(appointmentId, authenticatedUser.getSubject(), sickLeaveForm);
        return "redirect:/doctor/appointments/" + appointmentId;
    }

    @PostMapping("/{appointmentId}/complete")
    public String completeAppointment(@PathVariable("appointmentId") Long appointmentId,
                                      @Valid @ModelAttribute("completeForm") AppointmentCompleteDTO completeForm,
                                      BindingResult bindingResult,
                                      @AuthenticationPrincipal OidcUser authenticatedUser,
                                      RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please fix the form fields.");
            return "redirect:/doctor/appointments/" + appointmentId;
        }

        try {
            appointmentService.completeAppointment(appointmentId, authenticatedUser.getSubject(), completeForm);
            redirectAttributes.addFlashAttribute("success", "Appointment saved.");
            return "redirect:/doctor/appointments";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/doctor/appointments/" + appointmentId;
        }
    }


}