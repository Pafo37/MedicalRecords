package com.medicalrecords.service.statistics;


import com.medicalrecords.data.dto.DiagnosisPatientCountDTO;
import com.medicalrecords.data.dto.DoctorAppointmentCountDTO;
import com.medicalrecords.data.dto.DoctorPrimaryPatientCountDTO;

import java.util.List;

public interface StatisticsService {

    List<DiagnosisPatientCountDTO> getDiagnosisPatientCounts();

    List<DoctorPrimaryPatientCountDTO> getDoctorPrimaryPatientCounts();

    List<DoctorAppointmentCountDTO> getDoctorAppointmentCounts();
}