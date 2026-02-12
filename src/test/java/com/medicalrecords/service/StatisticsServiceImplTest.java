package com.medicalrecords.service;

import com.medicalrecords.data.dto.DiagnosisPatientCountDTO;
import com.medicalrecords.data.dto.DoctorAppointmentCountDTO;
import com.medicalrecords.data.dto.DoctorPrimaryPatientCountDTO;
import com.medicalrecords.data.repository.AppointmentRepository;
import com.medicalrecords.data.repository.PatientRepository;
import com.medicalrecords.service.statistics.StatisticsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    @Test
    void getDiagnosisPatientCounts_shouldMapRepositoryRowsToDiagnosisPatientCountDTOList() {
        List<Object[]> repositoryRows = List.of(
                new Object[]{"flu", 5L},
                new Object[]{"cold", 2}
        );

        when(appointmentRepository.findDistinctPatientCountsGroupedByDiagnosisName())
                .thenReturn(repositoryRows);

        List<DiagnosisPatientCountDTO> result = statisticsService.getDiagnosisPatientCounts();

        assertEquals(2, result.size());

        DiagnosisPatientCountDTO firstRow = result.get(0);
        assertEquals("flu", firstRow.getDiagnosisName());
        assertEquals(5L, firstRow.getDistinctPatientCount());

        DiagnosisPatientCountDTO secondRow = result.get(1);
        assertEquals("cold", secondRow.getDiagnosisName());
        assertEquals(2L, secondRow.getDistinctPatientCount());

        verify(appointmentRepository).findDistinctPatientCountsGroupedByDiagnosisName();
        verifyNoMoreInteractions(appointmentRepository);
        verifyNoInteractions(patientRepository);
    }

    @Test
    void getDoctorPrimaryPatientCounts_shouldMapRepositoryRowsToDoctorPrimaryPatientCountDTOList() {
        List<Object[]> repositoryRows = List.of(
                new Object[]{10L, "MED-010", "Ivan", "Ivanov", 12L},
                new Object[]{11, "MED-011", "Maria", "Petrova", 3}
        );

        when(patientRepository.findPrimaryPatientCountsGroupedByDoctor())
                .thenReturn(repositoryRows);

        List<DoctorPrimaryPatientCountDTO> result = statisticsService.getDoctorPrimaryPatientCounts();

        assertEquals(2, result.size());

        DoctorPrimaryPatientCountDTO firstRow = result.get(0);
        assertEquals(10L, firstRow.getDoctorId());
        assertEquals("MED-010", firstRow.getDoctorMedicalId());
        assertEquals("Ivan", firstRow.getDoctorFirstName());
        assertEquals("Ivanov", firstRow.getDoctorLastName());
        assertEquals(12L, firstRow.getPrimaryPatientCount());

        DoctorPrimaryPatientCountDTO secondRow = result.get(1);
        assertEquals(11L, secondRow.getDoctorId());
        assertEquals("MED-011", secondRow.getDoctorMedicalId());
        assertEquals("Maria", secondRow.getDoctorFirstName());
        assertEquals("Petrova", secondRow.getDoctorLastName());
        assertEquals(3L, secondRow.getPrimaryPatientCount());

        verify(patientRepository).findPrimaryPatientCountsGroupedByDoctor();
        verifyNoMoreInteractions(patientRepository);
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void getDoctorAppointmentCounts_shouldMapRepositoryRowsToDoctorAppointmentCountDTOList() {
        List<Object[]> repositoryRows = List.of(
                new Object[]{20L, "MED-020", "Georgi", "Georgiev", 15L, 9L},
                new Object[]{21, "MED-021", "Anna", "Ivanova", 4, 0}
        );

        when(appointmentRepository.findAppointmentCountsGroupedByDoctor())
                .thenReturn(repositoryRows);

        List<DoctorAppointmentCountDTO> result = statisticsService.getDoctorAppointmentCounts();

        assertEquals(2, result.size());

        DoctorAppointmentCountDTO firstRow = result.get(0);
        assertEquals(20L, firstRow.getDoctorId());
        assertEquals("MED-020", firstRow.getDoctorMedicalId());
        assertEquals("Georgi", firstRow.getDoctorFirstName());
        assertEquals("Georgiev", firstRow.getDoctorLastName());
        assertEquals(15L, firstRow.getTotalAppointmentCount());
        assertEquals(9L, firstRow.getCompletedAppointmentCount());

        DoctorAppointmentCountDTO secondRow = result.get(1);
        assertEquals(21L, secondRow.getDoctorId());
        assertEquals("MED-021", secondRow.getDoctorMedicalId());
        assertEquals("Anna", secondRow.getDoctorFirstName());
        assertEquals("Ivanova", secondRow.getDoctorLastName());
        assertEquals(4L, secondRow.getTotalAppointmentCount());
        assertEquals(0L, secondRow.getCompletedAppointmentCount());

        verify(appointmentRepository).findAppointmentCountsGroupedByDoctor();
        verifyNoMoreInteractions(appointmentRepository);
        verifyNoInteractions(patientRepository);
    }
}