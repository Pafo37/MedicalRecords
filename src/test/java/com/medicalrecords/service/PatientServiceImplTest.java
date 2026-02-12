package com.medicalrecords.service;

import com.medicalrecords.data.dto.CreatePatientDTO;
import com.medicalrecords.data.dto.RegistrationDTO;
import com.medicalrecords.data.entity.Doctor;
import com.medicalrecords.data.entity.Patient;
import com.medicalrecords.data.entity.User;
import com.medicalrecords.data.repository.DoctorRepository;
import com.medicalrecords.data.repository.PatientRepository;
import com.medicalrecords.service.patient.PatientServiceImpl;
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
class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private PatientServiceImpl patientService;

    @Captor
    private ArgumentCaptor<Patient> patientCaptor;

    @Test
    void createFromRegistration_shouldSavePatientWithExpectedFields() {
        RegistrationDTO registrationDTO = mock(RegistrationDTO.class);
        User user = new User();

        when(registrationDTO.getFirstName()).thenReturn("Maria");
        when(registrationDTO.getLastName()).thenReturn("Petrova");
        when(registrationDTO.getEgn()).thenReturn("1234567890");

        patientService.createFromRegistration(registrationDTO, user);

        verify(patientRepository).save(patientCaptor.capture());
        Patient savedPatient = patientCaptor.getValue();

        assertEquals(user, savedPatient.getUser());
        assertEquals("Maria", savedPatient.getFirstName());
        assertEquals("Petrova", savedPatient.getLastName());
        assertEquals("1234567890", savedPatient.getEgn());
        assertFalse(savedPatient.isInsurancePaidLast6Months());
    }

    @Test
    void create_shouldThrowIllegalArgumentException_whenFirstNameIsNull() {
        CreatePatientDTO createPatientDTO = mock(CreatePatientDTO.class);
        when(createPatientDTO.getFirstName()).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> patientService.create(createPatientDTO)
        );

        assertEquals("firstName is required", exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void create_shouldThrowIllegalArgumentException_whenFirstNameIsBlank() {
        CreatePatientDTO createPatientDTO = mock(CreatePatientDTO.class);
        when(createPatientDTO.getFirstName()).thenReturn("   ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> patientService.create(createPatientDTO)
        );

        assertEquals("firstName is required", exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void create_shouldThrowIllegalArgumentException_whenLastNameIsNull() {
        CreatePatientDTO createPatientDTO = mock(CreatePatientDTO.class);

        when(createPatientDTO.getFirstName()).thenReturn("Maria");
        when(createPatientDTO.getLastName()).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> patientService.create(createPatientDTO)
        );

        assertEquals("lastName is required", exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void create_shouldThrowIllegalArgumentException_whenLastNameIsBlank() {
        CreatePatientDTO createPatientDTO = mock(CreatePatientDTO.class);

        when(createPatientDTO.getFirstName()).thenReturn("Maria");
        when(createPatientDTO.getLastName()).thenReturn("   ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> patientService.create(createPatientDTO)
        );

        assertEquals("lastName is required", exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void create_shouldThrowIllegalArgumentException_whenEgnIsNull() {
        CreatePatientDTO createPatientDTO = mock(CreatePatientDTO.class);

        when(createPatientDTO.getFirstName()).thenReturn("Maria");
        when(createPatientDTO.getLastName()).thenReturn("Petrova");
        when(createPatientDTO.getEgn()).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> patientService.create(createPatientDTO)
        );

        assertEquals("egn is required", exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void create_shouldThrowIllegalArgumentException_whenEgnIsBlank() {
        CreatePatientDTO createPatientDTO = mock(CreatePatientDTO.class);

        when(createPatientDTO.getFirstName()).thenReturn("Maria");
        when(createPatientDTO.getLastName()).thenReturn("Petrova");
        when(createPatientDTO.getEgn()).thenReturn("   ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> patientService.create(createPatientDTO)
        );

        assertEquals("egn is required", exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void create_shouldThrowIllegalArgumentException_whenPrimaryCareDoctorIdIsNull() {
        CreatePatientDTO createPatientDTO = mock(CreatePatientDTO.class);

        when(createPatientDTO.getFirstName()).thenReturn("Maria");
        when(createPatientDTO.getLastName()).thenReturn("Petrova");
        when(createPatientDTO.getEgn()).thenReturn("1234567890");
        when(createPatientDTO.getPrimaryCareDoctorId()).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> patientService.create(createPatientDTO)
        );

        assertEquals("primaryCareDoctorId is required", exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void create_shouldThrowIllegalArgumentException_whenPatientWithEgnAlreadyExists() {
        CreatePatientDTO createPatientDTO = mock(CreatePatientDTO.class);

        when(createPatientDTO.getFirstName()).thenReturn("Maria");
        when(createPatientDTO.getLastName()).thenReturn("Petrova");
        when(createPatientDTO.getEgn()).thenReturn("1234567890");
        when(createPatientDTO.getPrimaryCareDoctorId()).thenReturn(10L);

        Patient existingPatient = new Patient();
        existingPatient.setId(55L);
        existingPatient.setEgn("1234567890");

        when(patientRepository.findByEgn("1234567890")).thenReturn(Optional.of(existingPatient));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> patientService.create(createPatientDTO)
        );

        assertEquals("Patient with this EGN already exists", exception.getMessage());
        verify(doctorRepository, never()).findById(anyLong());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void create_shouldThrowIllegalArgumentException_whenPrimaryCareDoctorNotFound() {
        CreatePatientDTO createPatientDTO = mock(CreatePatientDTO.class);

        when(createPatientDTO.getFirstName()).thenReturn("Maria");
        when(createPatientDTO.getLastName()).thenReturn("Petrova");
        when(createPatientDTO.getEgn()).thenReturn("1234567890");
        when(createPatientDTO.getPrimaryCareDoctorId()).thenReturn(10L);

        when(patientRepository.findByEgn("1234567890")).thenReturn(Optional.empty());
        when(doctorRepository.findById(10L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> patientService.create(createPatientDTO)
        );

        assertEquals("Primary care doctor not found", exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void create_shouldSavePatientAndReturnId_whenInputIsValid() {
        CreatePatientDTO createPatientDTO = mock(CreatePatientDTO.class);

        when(createPatientDTO.getFirstName()).thenReturn("Maria");
        when(createPatientDTO.getLastName()).thenReturn("Petrova");
        when(createPatientDTO.getEgn()).thenReturn("1234567890");
        when(createPatientDTO.getPrimaryCareDoctorId()).thenReturn(10L);

        when(patientRepository.findByEgn("1234567890")).thenReturn(Optional.empty());

        Doctor primaryCareDoctor = new Doctor();
        primaryCareDoctor.setId(10L);
        primaryCareDoctor.setMedicalId("MED-010");
        primaryCareDoctor.setFirstName("Ivan");
        primaryCareDoctor.setLastName("Ivanov");

        when(doctorRepository.findById(10L)).thenReturn(Optional.of(primaryCareDoctor));

        Patient savedPatient = new Patient();
        savedPatient.setId(99L);
        savedPatient.setFirstName("Maria");
        savedPatient.setLastName("Petrova");
        savedPatient.setEgn("1234567890");
        savedPatient.setPrimaryCareDoctor(primaryCareDoctor);

        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);

        Long createdPatientId = patientService.create(createPatientDTO);

        assertEquals(99L, createdPatientId);

        verify(patientRepository).save(patientCaptor.capture());
        Patient patientToSave = patientCaptor.getValue();

        assertEquals("Maria", patientToSave.getFirstName());
        assertEquals("Petrova", patientToSave.getLastName());
        assertEquals("1234567890", patientToSave.getEgn());
        assertEquals(primaryCareDoctor, patientToSave.getPrimaryCareDoctor());
    }

    @Test
    void getById_shouldReturnPatient_whenPatientExists() {
        Long patientId = 10L;

        Patient patient = new Patient();
        patient.setId(patientId);
        patient.setFirstName("Maria");
        patient.setLastName("Petrova");
        patient.setEgn("1234567890");

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        Patient result = patientService.getById(patientId);

        assertNotNull(result);
        assertEquals(patientId, result.getId());
        assertEquals("Maria", result.getFirstName());
        assertEquals("Petrova", result.getLastName());
        assertEquals("1234567890", result.getEgn());
    }

    @Test
    void getById_shouldThrowIllegalArgumentException_whenPatientDoesNotExist() {
        Long patientId = 999L;
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> patientService.getById(patientId)
        );

        assertEquals("Patient not found", exception.getMessage());
    }

    @Test
    void getAll_shouldReturnAllPatients() {
        Patient firstPatient = new Patient();
        firstPatient.setId(1L);
        firstPatient.setFirstName("Maria");
        firstPatient.setLastName("Petrova");
        firstPatient.setEgn("1234567890");

        Patient secondPatient = new Patient();
        secondPatient.setId(2L);
        secondPatient.setFirstName("Georgi");
        secondPatient.setLastName("Georgiev");
        secondPatient.setEgn("0987654321");

        when(patientRepository.findAll()).thenReturn(List.of(firstPatient, secondPatient));

        List<Patient> patients = patientService.getAll();

        assertEquals(2, patients.size());
        assertEquals("Maria", patients.get(0).getFirstName());
        assertEquals("Georgi", patients.get(1).getFirstName());
        verify(patientRepository).findAll();
    }
}