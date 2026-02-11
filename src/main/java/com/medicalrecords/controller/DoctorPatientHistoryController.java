package com.medicalrecords.controller;

import com.medicalrecords.data.entity.Appointment;
import com.medicalrecords.data.entity.Patient;
import com.medicalrecords.data.repository.AppointmentRepository;
import com.medicalrecords.data.repository.InsuranceMonthRepository;
import com.medicalrecords.data.repository.PatientRepository;
import com.medicalrecords.data.repository.SickLeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/doctor/patients")
@RequiredArgsConstructor
public class DoctorPatientHistoryController {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final SickLeaveRepository sickLeaveRepository;
    private final InsuranceMonthRepository insuranceMonthRepository;

    @GetMapping("/{patientId}/history")
    public String viewPatientHistory(@PathVariable("patientId") Long patientId,
                                     @AuthenticationPrincipal OidcUser authenticatedUser,
                                     Model model) {

        String doctorKeycloakId = authenticatedUser.getSubject();

        boolean doctorHasAccess =
                appointmentRepository.existsByDoctor_User_KeycloakIdAndPatient_Id(doctorKeycloakId, patientId);

        if (!doctorHasAccess) {
            throw new SecurityException("You do not have access to this patient's history.");
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));

        List<Appointment> completedAppointments =
                appointmentRepository.findAllByPatient_IdAndCompletedTrueOrderByVisitDateDesc(patientId);

        model.addAttribute("patient", patient);
        model.addAttribute("appointments", completedAppointments);

        model.addAttribute("insuranceMonths",
                insuranceMonthRepository.findAllByPatient_IdOrderByMonthValueDesc(patientId));

        return "doctor_patient_history";
    }
}
