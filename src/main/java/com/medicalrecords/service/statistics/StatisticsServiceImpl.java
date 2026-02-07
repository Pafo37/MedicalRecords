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

    @Override
    public List<PatientsPerDiagnosisStatisticsDTO> getPatientsPerDiagnosisStatistics() {
        List<Object[]> rows = appointmentRepository.countDistinctPatientsPerDiagnosis();
        List<PatientsPerDiagnosisStatisticsDTO> result = new ArrayList<>();

        for (Object[] row : rows) {
            Long diagnosisId = (Long) row[0];
            String diagnosisName = (String) row[1];
            Long patientsCount = (Long) row[2];

            result.add(new PatientsPerDiagnosisStatisticsDTO(diagnosisId, diagnosisName, patientsCount));
        }

        return result;
    }

    @Override
    public List<PatientsPerDoctorStatisticsDTO> getPatientsPerDoctorStatistics() {
        List<Object[]> rows = patientRepository.countPatientsPerPrimaryCareDoctor();
        List<PatientsPerDoctorStatisticsDTO> result = new ArrayList<>();

        for (Object[] row : rows) {
            Long doctorId = (Long) row[0];
            String medicalId = (String) row[1];
            String firstName = (String) row[2];
            String lastName = (String) row[3];
            Long patientsCount = (Long) row[4];

            result.add(new PatientsPerDoctorStatisticsDTO(doctorId, medicalId, firstName, lastName, patientsCount));
        }

        return result;
    }

    @Override
    public List<AppointmentsPerDoctorStatisticsDTO> getAppointmentsPerDoctorStatistics() {
        List<Object[]> rows = appointmentRepository.countAppointmentsPerDoctor();
        List<AppointmentsPerDoctorStatisticsDTO> result = new ArrayList<>();

        for (Object[] row : rows) {
            Long doctorId = (Long) row[0];
            String medicalId = (String) row[1];
            String firstName = (String) row[2];
            String lastName = (String) row[3];
            Long appointmentsCount = (Long) row[4];

            result.add(new AppointmentsPerDoctorStatisticsDTO(doctorId, medicalId, firstName, lastName, appointmentsCount));
        }

        return result;
    }
}
