package com.medicalrecords.api;

import com.medicalrecords.data.dto.DiagnosisPatientCountDTO;
import com.medicalrecords.data.dto.DoctorAppointmentCountDTO;
import com.medicalrecords.data.dto.DoctorPrimaryPatientCountDTO;
import com.medicalrecords.service.statistics.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/doctor/statistics")
public class StatisticsRestController {

    private final StatisticsService statisticsService;

    @GetMapping("/diagnosis_statistics")
    public List<DiagnosisPatientCountDTO> getDiagnosisStatistics() {
        return statisticsService.getDiagnosisPatientCounts();
    }

    @GetMapping("/doctor_primary_patient_statistics")
    public List<DoctorPrimaryPatientCountDTO> getDoctorPrimaryPatientStatistics() {
        return statisticsService.getDoctorPrimaryPatientCounts();
    }

    @GetMapping("/doctor_appointment_statistics")
    public List<DoctorAppointmentCountDTO> getDoctorAppointmentStatistics() {
        return statisticsService.getDoctorAppointmentCounts();
    }
}