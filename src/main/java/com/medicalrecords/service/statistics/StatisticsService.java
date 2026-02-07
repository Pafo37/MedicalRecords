package com.medicalrecords.service.statistics;


import com.medicalrecords.data.dto.AppointmentsPerDoctorStatisticsDTO;
import com.medicalrecords.data.dto.PatientsPerDiagnosisStatisticsDTO;
import com.medicalrecords.data.dto.PatientsPerDoctorStatisticsDTO;

import java.util.List;

public interface StatisticsService {

    List<PatientsPerDiagnosisStatisticsDTO> getPatientsPerDiagnosisStatistics();

    List<PatientsPerDoctorStatisticsDTO> getPatientsPerDoctorStatistics();

    List<AppointmentsPerDoctorStatisticsDTO> getAppointmentsPerDoctorStatistics();
}