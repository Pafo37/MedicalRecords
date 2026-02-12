package com.medicalrecords.service;

import com.medicalrecords.data.dto.CreateDiagnosisDTO;
import com.medicalrecords.data.entity.Diagnosis;
import com.medicalrecords.data.repository.DiagnosisRepository;
import com.medicalrecords.service.diagnosis.DiagnosisServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiagnosisServiceImplTest {

    @Mock
    private DiagnosisRepository diagnosisRepository;

    @InjectMocks
    private DiagnosisServiceImpl diagnosisService;

    @Captor
    private ArgumentCaptor<Diagnosis> diagnosisCaptor;

    @Test
    void create_shouldThrowIllegalArgumentException_whenNameIsNull() {
        CreateDiagnosisDTO createDiagnosisDTO = mock(CreateDiagnosisDTO.class);
        when(createDiagnosisDTO.getName()).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> diagnosisService.create(createDiagnosisDTO)
        );

        assertEquals("name is required", exception.getMessage());
        verify(diagnosisRepository, never()).save(any(Diagnosis.class));
    }

    @Test
    void create_shouldThrowIllegalArgumentException_whenNameIsBlank() {
        CreateDiagnosisDTO createDiagnosisDTO = mock(CreateDiagnosisDTO.class);
        when(createDiagnosisDTO.getName()).thenReturn("   ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> diagnosisService.create(createDiagnosisDTO)
        );

        assertEquals("name is required", exception.getMessage());
        verify(diagnosisRepository, never()).save(any(Diagnosis.class));
    }

    @Test
    void create_shouldSaveDiagnosisAndReturnId_whenNameIsProvided() {
        CreateDiagnosisDTO createDiagnosisDTO = mock(CreateDiagnosisDTO.class);
        when(createDiagnosisDTO.getName()).thenReturn("Influenza");

        Diagnosis savedDiagnosis = new Diagnosis();
        savedDiagnosis.setId(25L);
        savedDiagnosis.setName("Influenza");

        when(diagnosisRepository.save(any(Diagnosis.class))).thenReturn(savedDiagnosis);

        Long createdDiagnosisId = diagnosisService.create(createDiagnosisDTO);

        assertEquals(25L, createdDiagnosisId);

        verify(diagnosisRepository).save(diagnosisCaptor.capture());
        Diagnosis diagnosisToSave = diagnosisCaptor.getValue();
        assertEquals("Influenza", diagnosisToSave.getName());
    }

    @Test
    void getById_shouldReturnDiagnosis_whenDiagnosisExists() {
        Long diagnosisId = 10L;

        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setId(diagnosisId);
        diagnosis.setName("Flu");

        when(diagnosisRepository.findById(diagnosisId)).thenReturn(Optional.of(diagnosis));

        Diagnosis result = diagnosisService.getById(diagnosisId);

        assertNotNull(result);
        assertEquals(diagnosisId, result.getId());
        assertEquals("Flu", result.getName());
    }

    @Test
    void getById_shouldThrowIllegalArgumentException_whenDiagnosisDoesNotExist() {
        Long diagnosisId = 999L;
        when(diagnosisRepository.findById(diagnosisId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> diagnosisService.getById(diagnosisId)
        );

        assertEquals("Diagnosis not found", exception.getMessage());
    }

    @Test
    void getAll_shouldReturnAllDiagnoses() {
        Diagnosis firstDiagnosis = new Diagnosis();
        firstDiagnosis.setId(1L);
        firstDiagnosis.setName("Flu");

        Diagnosis secondDiagnosis = new Diagnosis();
        secondDiagnosis.setId(2L);
        secondDiagnosis.setName("Cold");

        when(diagnosisRepository.findAll()).thenReturn(List.of(firstDiagnosis, secondDiagnosis));

        List<Diagnosis> diagnoses = diagnosisService.getAll();

        assertEquals(2, diagnoses.size());
        assertEquals("Flu", diagnoses.get(0).getName());
        assertEquals("Cold", diagnoses.get(1).getName());
        verify(diagnosisRepository).findAll();
    }
}