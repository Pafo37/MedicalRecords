package com.medicalrecords.service;

import com.medicalrecords.data.dto.CreateDoctorDTO;
import com.medicalrecords.data.dto.RegistrationDTO;
import com.medicalrecords.data.entity.Doctor;
import com.medicalrecords.data.entity.User;
import com.medicalrecords.data.repository.DoctorRepository;
import com.medicalrecords.service.doctor.DoctorServiceImpl;
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
class DoctorServiceImplTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    @Captor
    private ArgumentCaptor<Doctor> doctorCaptor;

    @Test
    void createFromRegistration_shouldSaveDoctorWithExpectedFields() {
        RegistrationDTO registrationDTO = mock(RegistrationDTO.class);
        User user = new User();

        when(registrationDTO.getMedicalId()).thenReturn("MED-123");
        when(registrationDTO.getSpecialty()).thenReturn("Cardiology");
        when(registrationDTO.getFirstName()).thenReturn("John");
        when(registrationDTO.getLastName()).thenReturn("Smith");

        doctorService.createFromRegistration(registrationDTO, user);

        verify(doctorRepository).save(doctorCaptor.capture());
        Doctor savedDoctor = doctorCaptor.getValue();

        assertEquals(user, savedDoctor.getUser());
        assertEquals("MED-123", savedDoctor.getMedicalId());
        assertEquals("Cardiology", savedDoctor.getSpecialty());
        assertEquals("John", savedDoctor.getFirstName());
        assertEquals("Smith", savedDoctor.getLastName());
        assertFalse(savedDoctor.isPersonalDoctor());
    }

    @Test
    void create_shouldThrowIllegalArgumentException_whenMedicalIdIsNull() {
        CreateDoctorDTO createDoctorDTO = mock(CreateDoctorDTO.class);
        when(createDoctorDTO.getMedicalId()).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> doctorService.create(createDoctorDTO)
        );

        assertEquals("medicalId is required", exception.getMessage());
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void create_shouldThrowIllegalArgumentException_whenMedicalIdIsBlank() {
        CreateDoctorDTO createDoctorDTO = mock(CreateDoctorDTO.class);
        when(createDoctorDTO.getMedicalId()).thenReturn("   ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> doctorService.create(createDoctorDTO)
        );

        assertEquals("medicalId is required", exception.getMessage());
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void create_shouldThrowIllegalArgumentException_whenFirstNameIsNull() {
        CreateDoctorDTO createDoctorDTO = mock(CreateDoctorDTO.class);

        when(createDoctorDTO.getMedicalId()).thenReturn("MED-123");
        when(createDoctorDTO.getFirstName()).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> doctorService.create(createDoctorDTO)
        );

        assertEquals("firstName is required", exception.getMessage());
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void create_shouldThrowIllegalArgumentException_whenFirstNameIsBlank() {
        CreateDoctorDTO createDoctorDTO = mock(CreateDoctorDTO.class);

        when(createDoctorDTO.getMedicalId()).thenReturn("MED-123");
        when(createDoctorDTO.getFirstName()).thenReturn("   ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> doctorService.create(createDoctorDTO)
        );

        assertEquals("firstName is required", exception.getMessage());
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void create_shouldThrowIllegalArgumentException_whenLastNameIsNull() {
        CreateDoctorDTO createDoctorDTO = mock(CreateDoctorDTO.class);

        when(createDoctorDTO.getMedicalId()).thenReturn("MED-123");
        when(createDoctorDTO.getFirstName()).thenReturn("John");
        when(createDoctorDTO.getLastName()).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> doctorService.create(createDoctorDTO)
        );

        assertEquals("lastName is required", exception.getMessage());
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void create_shouldThrowIllegalArgumentException_whenLastNameIsBlank() {
        CreateDoctorDTO createDoctorDTO = mock(CreateDoctorDTO.class);

        when(createDoctorDTO.getMedicalId()).thenReturn("MED-123");
        when(createDoctorDTO.getFirstName()).thenReturn("John");
        when(createDoctorDTO.getLastName()).thenReturn("   ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> doctorService.create(createDoctorDTO)
        );

        assertEquals("lastName is required", exception.getMessage());
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void create_shouldThrowIllegalArgumentException_whenDoctorWithMedicalIdAlreadyExists() {
        CreateDoctorDTO createDoctorDTO = mock(CreateDoctorDTO.class);

        when(createDoctorDTO.getMedicalId()).thenReturn("MED-123");
        when(createDoctorDTO.getFirstName()).thenReturn("John");
        when(createDoctorDTO.getLastName()).thenReturn("Smith");

        Doctor existingDoctor = new Doctor();
        existingDoctor.setId(50L);
        existingDoctor.setMedicalId("MED-123");

        when(doctorRepository.findByMedicalId("MED-123")).thenReturn(Optional.of(existingDoctor));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> doctorService.create(createDoctorDTO)
        );

        assertEquals("Doctor with this medicalId already exists", exception.getMessage());
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void create_shouldSaveDoctorAndReturnId_whenInputIsValidAndMedicalIdIsUnique() {
        CreateDoctorDTO createDoctorDTO = mock(CreateDoctorDTO.class);

        when(createDoctorDTO.getMedicalId()).thenReturn("MED-123");
        when(createDoctorDTO.getFirstName()).thenReturn("John");
        when(createDoctorDTO.getLastName()).thenReturn("Smith");

        when(doctorRepository.findByMedicalId("MED-123")).thenReturn(Optional.empty());

        Doctor savedDoctor = new Doctor();
        savedDoctor.setId(99L);
        savedDoctor.setMedicalId("MED-123");
        savedDoctor.setFirstName("John");
        savedDoctor.setLastName("Smith");

        when(doctorRepository.save(any(Doctor.class))).thenReturn(savedDoctor);

        Long createdDoctorId = doctorService.create(createDoctorDTO);

        assertEquals(99L, createdDoctorId);

        verify(doctorRepository).save(doctorCaptor.capture());
        Doctor doctorToSave = doctorCaptor.getValue();

        assertEquals("MED-123", doctorToSave.getMedicalId());
        assertEquals("John", doctorToSave.getFirstName());
        assertEquals("Smith", doctorToSave.getLastName());
    }

    @Test
    void getById_shouldReturnDoctor_whenDoctorExists() {
        Long doctorId = 10L;

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setMedicalId("MED-123");
        doctor.setFirstName("John");
        doctor.setLastName("Smith");

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        Doctor result = doctorService.getById(doctorId);

        assertNotNull(result);
        assertEquals(doctorId, result.getId());
        assertEquals("MED-123", result.getMedicalId());
        assertEquals("John", result.getFirstName());
        assertEquals("Smith", result.getLastName());
    }

    @Test
    void getById_shouldThrowIllegalArgumentException_whenDoctorDoesNotExist() {
        Long doctorId = 999L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> doctorService.getById(doctorId)
        );

        assertEquals("Doctor not found", exception.getMessage());
    }

    @Test
    void getAll_shouldReturnAllDoctors() {
        Doctor firstDoctor = new Doctor();
        firstDoctor.setId(1L);
        firstDoctor.setMedicalId("MED-001");
        firstDoctor.setFirstName("John");
        firstDoctor.setLastName("Smith");

        Doctor secondDoctor = new Doctor();
        secondDoctor.setId(2L);
        secondDoctor.setMedicalId("MED-002");
        secondDoctor.setFirstName("Anna");
        secondDoctor.setLastName("Ivanova");

        when(doctorRepository.findAll()).thenReturn(List.of(firstDoctor, secondDoctor));

        List<Doctor> doctors = doctorService.getAll();

        assertEquals(2, doctors.size());
        assertEquals("MED-001", doctors.get(0).getMedicalId());
        assertEquals("MED-002", doctors.get(1).getMedicalId());
        verify(doctorRepository).findAll();
    }
}