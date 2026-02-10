package com.medicalrecords.service.statistics;

import com.medicalrecords.data.dto.AppointmentsPerDoctorStatisticsDTO;
import com.medicalrecords.data.dto.PatientsPerDiagnosisStatisticsDTO;
import com.medicalrecords.data.dto.PatientsPerDoctorStatisticsDTO;
import com.medicalrecords.data.repository.AppointmentRepository;
import com.medicalrecords.data.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;


}
