package com.medicalrecords.controller;

import com.medicalrecords.data.dto.AppointmentsPerDoctorStatisticsDTO;
import com.medicalrecords.data.dto.PatientsPerDiagnosisStatisticsDTO;
import com.medicalrecords.data.dto.PatientsPerDoctorStatisticsDTO;
import com.medicalrecords.service.statistics.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/patients-per-diagnosis")
    public List<PatientsPerDiagnosisStatisticsDTO> getPatientsPerDiagnosisStatistics() {
        return statisticsService.getPatientsPerDiagnosisStatistics();
    }

    @GetMapping("/patients-per-doctor")
    public List<PatientsPerDoctorStatisticsDTO> getPatientsPerDoctorStatistics() {
        return statisticsService.getPatientsPerDoctorStatistics();
    }

    @GetMapping("/appointments-per-doctor")
    public List<AppointmentsPerDoctorStatisticsDTO> getAppointmentsPerDoctorStatistics() {
        return statisticsService.getAppointmentsPerDoctorStatistics();
    }
}