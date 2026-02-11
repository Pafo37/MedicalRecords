package com.medicalrecords.service.statistics;

import com.medicalrecords.data.dto.DiagnosisPatientCountDTO;
import com.medicalrecords.data.dto.DoctorAppointmentCountDTO;
import com.medicalrecords.data.dto.DoctorPrimaryPatientCountDTO;
import com.medicalrecords.data.repository.AppointmentRepository;
import com.medicalrecords.data.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {


    private final AppointmentRepository appointmentRepository;

    private final PatientRepository patientRepository;

    @Override
    public List<DiagnosisPatientCountDTO> getDiagnosisPatientCounts() {

        List<Object[]> queryResults =
                appointmentRepository.findDistinctPatientCountsGroupedByDiagnosisName();

        return queryResults.stream()
                .map(resultRow -> {

                    String diagnosisName = (String) resultRow[0];

                    Long distinctPatientCount =
                            ((Number) resultRow[1]).longValue();

                    return new DiagnosisPatientCountDTO(
                            diagnosisName,
                            distinctPatientCount
                    );
                })
                .toList();
    }

    @Override
    public List<DoctorPrimaryPatientCountDTO> getDoctorPrimaryPatientCounts() {

        List<Object[]> queryResults =
                patientRepository.findPrimaryPatientCountsGroupedByDoctor();

        return queryResults.stream()
                .map(resultRow -> {

                    Long doctorId =
                            ((Number) resultRow[0]).longValue();

                    String doctorMedicalId =
                            (String) resultRow[1];

                    String doctorFirstName =
                            (String) resultRow[2];

                    String doctorLastName =
                            (String) resultRow[3];

                    Long primaryPatientCount =
                            ((Number) resultRow[4]).longValue();

                    return new DoctorPrimaryPatientCountDTO(
                            doctorId,
                            doctorMedicalId,
                            doctorFirstName,
                            doctorLastName,
                            primaryPatientCount
                    );
                })
                .toList();
    }

    @Override
    public List<DoctorAppointmentCountDTO> getDoctorAppointmentCounts() {

        List<Object[]> queryResults =
                appointmentRepository.findAppointmentCountsGroupedByDoctor();

        return queryResults.stream()
                .map(resultRow -> {

                    Long doctorId =
                            ((Number) resultRow[0]).longValue();

                    String doctorMedicalId =
                            (String) resultRow[1];

                    String doctorFirstName =
                            (String) resultRow[2];

                    String doctorLastName =
                            (String) resultRow[3];

                    Long totalAppointmentCount =
                            ((Number) resultRow[4]).longValue();

                    Long completedAppointmentCount;

                    Object completedAppointmentCountRawValue =
                            resultRow[5];

                    if (completedAppointmentCountRawValue == null) {
                        completedAppointmentCount = 0L;
                    } else {
                        completedAppointmentCount =
                                ((Number) completedAppointmentCountRawValue).longValue();
                    }

                    return new DoctorAppointmentCountDTO(
                            doctorId,
                            doctorMedicalId,
                            doctorFirstName,
                            doctorLastName,
                            totalAppointmentCount,
                            completedAppointmentCount
                    );
                })
                .toList();
    }
}
