package com.medicalrecords.controller;

import com.medicalrecords.service.statistics.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/doctor/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/diagnosis_statistics")
    public String diagnosisStatistics(Model model) {

        model.addAttribute(
                "diagnosisStatistics",
                statisticsService.getDiagnosisPatientCounts()
        );

        return "diagnosis_statistics";
    }

    @GetMapping("/doctor_primary_patient_statistics")
    public String doctorPrimaryPatientStatistics(Model model) {

        model.addAttribute(
                "doctorPrimaryPatientStatistics",
                statisticsService.getDoctorPrimaryPatientCounts()
        );

        return "doctor_primary_patient_statistics";
    }

    @GetMapping("/doctor_appointment_statistics")
    public String doctorAppointmentStatistics(Model model) {

        model.addAttribute(
                "doctorAppointmentStatistics",
                statisticsService.getDoctorAppointmentCounts()
        );

        return "doctor_appointment_statistics";
    }
}