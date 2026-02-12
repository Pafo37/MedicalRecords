package com.medicalrecords.service;

import com.medicalrecords.data.dto.AppointmentCompleteDTO;
import com.medicalrecords.data.dto.AppointmentDTO;
import com.medicalrecords.data.dto.PrescriptionDTO;
import com.medicalrecords.data.dto.SickLeaveDTO;
import com.medicalrecords.data.entity.Appointment;
import com.medicalrecords.data.entity.Diagnosis;
import com.medicalrecords.data.entity.Doctor;
import com.medicalrecords.data.entity.Patient;
import com.medicalrecords.data.entity.Prescription;
import com.medicalrecords.data.entity.SickLeave;
import com.medicalrecords.data.repository.AppointmentRepository;
import com.medicalrecords.data.repository.DiagnosisRepository;
import com.medicalrecords.data.repository.DoctorRepository;
import com.medicalrecords.data.repository.PatientRepository;
import com.medicalrecords.data.repository.PrescriptionRepository;
import com.medicalrecords.data.repository.SickLeaveRepository;
import com.medicalrecords.service.appointment.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private SickLeaveRepository sickLeaveRepository;

    @Mock
    private DiagnosisRepository diagnosisRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @Captor
    private ArgumentCaptor<Appointment> appointmentCaptor;

    @Captor
    private ArgumentCaptor<Prescription> prescriptionCaptor;

    @Captor
    private ArgumentCaptor<SickLeave> sickLeaveCaptor;

    @Captor
    private ArgumentCaptor<Diagnosis> diagnosisCaptor;

    private String patientKeycloakId;
    private String doctorKeycloakId;

    @BeforeEach
    void setUp() {
        patientKeycloakId = "patient-keycloak-id";
        doctorKeycloakId = "doctor-keycloak-id";
    }

    @Test
    void createAppointment_shouldSaveAppointment_whenPatientAndDoctorExist() {
        AppointmentDTO appointmentDTO = mock(AppointmentDTO.class);
        LocalDateTime visitDate = LocalDateTime.now().plusHours(2).withSecond(0).withNano(0);
        String patientNotes = "Patient notes";

        Long doctorId = 10L;
        Patient patient = buildPatient(1L);
        Doctor doctor = buildDoctor(doctorId);

        when(appointmentDTO.getDoctorId()).thenReturn(doctorId);
        when(appointmentDTO.getVisitDate()).thenReturn(visitDate);
        when(appointmentDTO.getNotes()).thenReturn(patientNotes);

        when(patientRepository.findByUser_KeycloakId(patientKeycloakId)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        Appointment savedAppointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .visitDate(visitDate)
                .notes(patientNotes)
                .completed(false)
                .build();

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        Appointment result = appointmentService.createAppointment(appointmentDTO, patientKeycloakId);

        assertNotNull(result);
        assertEquals(patient, result.getPatient());
        assertEquals(doctor, result.getDoctor());
        assertEquals(visitDate, result.getVisitDate());
        assertEquals(patientNotes, result.getNotes());

        verify(appointmentRepository).save(appointmentCaptor.capture());
        Appointment capturedAppointment = appointmentCaptor.getValue();
        assertEquals(patient, capturedAppointment.getPatient());
        assertEquals(doctor, capturedAppointment.getDoctor());
        assertEquals(visitDate, capturedAppointment.getVisitDate());
        assertEquals(patientNotes, capturedAppointment.getNotes());
    }

    @Test
    void createAppointment_shouldThrowIllegalStateException_whenPatientNotFound() {
        AppointmentDTO appointmentDTO = mock(AppointmentDTO.class);
        when(patientRepository.findByUser_KeycloakId(patientKeycloakId)).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> appointmentService.createAppointment(appointmentDTO, patientKeycloakId)
        );

        assertEquals("Current patient not found.", exception.getMessage());
        verify(doctorRepository, never()).findById(anyLong());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void createAppointment_shouldThrowIllegalArgumentException_whenDoctorNotFound() {
        AppointmentDTO appointmentDTO = mock(AppointmentDTO.class);
        Long doctorId = 10L;

        when(appointmentDTO.getDoctorId()).thenReturn(doctorId);
        when(patientRepository.findByUser_KeycloakId(patientKeycloakId)).thenReturn(Optional.of(buildPatient(1L)));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> appointmentService.createAppointment(appointmentDTO, patientKeycloakId)
        );

        assertEquals("Doctor not found.", exception.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void getAppointmentForDoctor_shouldThrowSecurityException_whenAppointmentNotFoundForDoctor() {
        Long appointmentId = 55L;
        when(appointmentRepository.findByIdAndDoctor_User_KeycloakId(appointmentId, doctorKeycloakId))
                .thenReturn(Optional.empty());

        SecurityException exception = assertThrows(
                SecurityException.class,
                () -> appointmentService.getAppointmentForDoctor(appointmentId, doctorKeycloakId)
        );

        assertEquals("Appointment not found or you are not assigned to it.", exception.getMessage());
    }

    @Test
    void updateDoctorNotes_shouldThrowIllegalStateException_whenAppointmentNotToday() {
        Long appointmentId = 101L;
        Appointment appointment = buildAppointmentForDoctor(appointmentId, LocalDate.now().minusDays(1).atTime(10, 0));

        when(appointmentRepository.findByIdAndDoctor_User_KeycloakId(appointmentId, doctorKeycloakId))
                .thenReturn(Optional.of(appointment));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> appointmentService.updateDoctorNotes(appointmentId, doctorKeycloakId, "New doctor notes")
        );

        assertEquals("You can complete an appointment only on its scheduled day.", exception.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void updateDoctorNotes_shouldSaveUpdatedNotes_whenAppointmentIsToday() {
        Long appointmentId = 102L;
        Appointment appointment = buildAppointmentForDoctor(appointmentId, LocalDate.now().atTime(11, 0));
        String doctorNotes = "Updated doctor notes";

        when(appointmentRepository.findByIdAndDoctor_User_KeycloakId(appointmentId, doctorKeycloakId))
                .thenReturn(Optional.of(appointment));

        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        appointmentService.updateDoctorNotes(appointmentId, doctorKeycloakId, doctorNotes);

        verify(appointmentRepository).save(appointmentCaptor.capture());
        Appointment savedAppointment = appointmentCaptor.getValue();
        assertEquals(doctorNotes, savedAppointment.getDoctorNotes());
    }

    @Test
    void addPrescription_shouldThrowIllegalStateException_whenAppointmentNotToday() {
        Long appointmentId = 201L;
        Appointment appointment = buildAppointmentForDoctor(appointmentId, LocalDate.now().minusDays(1).atTime(9, 30));
        PrescriptionDTO prescriptionDTO = mock(PrescriptionDTO.class);

        when(appointmentRepository.findByIdAndDoctor_User_KeycloakId(appointmentId, doctorKeycloakId))
                .thenReturn(Optional.of(appointment));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> appointmentService.addPrescription(appointmentId, doctorKeycloakId, prescriptionDTO)
        );

        assertEquals("You can complete an appointment only on its scheduled day.", exception.getMessage());
        verify(prescriptionRepository, never()).save(any());
    }

    @Test
    void addPrescription_shouldSavePrescription_whenAppointmentIsToday() {
        Long appointmentId = 202L;
        Appointment appointment = buildAppointmentForDoctor(appointmentId, LocalDate.now().atTime(14, 15));
        PrescriptionDTO prescriptionDTO = mock(PrescriptionDTO.class);

        String instructions = "Take twice daily";
        when(prescriptionDTO.getInstructions()).thenReturn(instructions);

        when(appointmentRepository.findByIdAndDoctor_User_KeycloakId(appointmentId, doctorKeycloakId))
                .thenReturn(Optional.of(appointment));

        appointmentService.addPrescription(appointmentId, doctorKeycloakId, prescriptionDTO);

        verify(prescriptionRepository).save(prescriptionCaptor.capture());
        Prescription savedPrescription = prescriptionCaptor.getValue();
        assertEquals(appointment, savedPrescription.getAppointment());
        assertEquals(instructions, savedPrescription.getInstructions());
    }

    @Test
    void upsertSickLeave_shouldThrowIllegalArgumentException_whenEndDateBeforeStartDate() {
        Long appointmentId = 301L;
        Appointment appointment = buildAppointmentForDoctor(appointmentId, LocalDate.now().atTime(10, 0));
        SickLeaveDTO sickLeaveDTO = mock(SickLeaveDTO.class);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.minusDays(1);

        when(sickLeaveDTO.getStartDate()).thenReturn(startDate);
        when(sickLeaveDTO.getEndDate()).thenReturn(endDate);

        when(appointmentRepository.findByIdAndDoctor_User_KeycloakId(appointmentId, doctorKeycloakId))
                .thenReturn(Optional.of(appointment));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> appointmentService.upsertSickLeave(appointmentId, doctorKeycloakId, sickLeaveDTO)
        );

        assertEquals("End date cannot be before start date.", exception.getMessage());
        verify(sickLeaveRepository, never()).save(any());
    }

    @Test
    void upsertSickLeave_shouldCreateNewSickLeave_whenNoneExists() {
        Long appointmentId = 302L;
        Appointment appointment = buildAppointmentForDoctor(appointmentId, LocalDate.now().atTime(10, 0));
        SickLeaveDTO sickLeaveDTO = mock(SickLeaveDTO.class);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(2);

        when(sickLeaveDTO.getStartDate()).thenReturn(startDate);
        when(sickLeaveDTO.getEndDate()).thenReturn(endDate);

        when(appointmentRepository.findByIdAndDoctor_User_KeycloakId(appointmentId, doctorKeycloakId))
                .thenReturn(Optional.of(appointment));

        when(sickLeaveRepository.findByAppointment_Id(appointmentId)).thenReturn(Optional.empty());

        appointmentService.upsertSickLeave(appointmentId, doctorKeycloakId, sickLeaveDTO);

        verify(sickLeaveRepository).save(sickLeaveCaptor.capture());
        SickLeave savedSickLeave = sickLeaveCaptor.getValue();
        assertEquals(appointment, savedSickLeave.getAppointment());
        assertEquals(startDate, savedSickLeave.getStartDate());
        assertEquals(endDate, savedSickLeave.getEndDate());
    }

    @Test
    void completeAppointment_shouldThrowIllegalStateException_whenAppointmentNotToday() {
        Long appointmentId = 401L;
        Appointment appointment = buildAppointmentForDoctor(appointmentId, LocalDate.now().minusDays(1).atTime(10, 0));
        AppointmentCompleteDTO completeDTO = mock(AppointmentCompleteDTO.class);

        when(appointmentRepository.findByIdAndDoctor_User_KeycloakId(appointmentId, doctorKeycloakId))
                .thenReturn(Optional.of(appointment));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> appointmentService.completeAppointment(appointmentId, doctorKeycloakId, completeDTO)
        );

        assertEquals("You can complete an appointment only on its scheduled day.", exception.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void completeAppointment_shouldThrowIllegalArgumentException_whenDiagnosisIsMissing() {
        Long appointmentId = 402L;
        Appointment appointment = buildAppointmentForDoctor(appointmentId, LocalDate.now().atTime(10, 0));
        AppointmentCompleteDTO completeDTO = mock(AppointmentCompleteDTO.class);

        when(completeDTO.getDoctorNotes()).thenReturn("Doctor notes");
        when(completeDTO.getPrescriptionInstructions()).thenReturn(null);
        when(completeDTO.getSickLeaveStartDate()).thenReturn(null);
        when(completeDTO.getSickLeaveEndDate()).thenReturn(null);
        when(completeDTO.getDiagnosis()).thenReturn("   ");

        when(appointmentRepository.findByIdAndDoctor_User_KeycloakId(appointmentId, doctorKeycloakId))
                .thenReturn(Optional.of(appointment));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> appointmentService.completeAppointment(appointmentId, doctorKeycloakId, completeDTO)
        );

        assertEquals("Diagnosis is required.", exception.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void completeAppointment_shouldCreatePrescription_whenPrescriptionInstructionsPresent() {
        Long appointmentId = 403L;
        Appointment appointment = buildAppointmentForDoctor(appointmentId, LocalDate.now().atTime(10, 0));
        AppointmentCompleteDTO completeDTO = mock(AppointmentCompleteDTO.class);

        when(completeDTO.getDoctorNotes()).thenReturn("Doctor notes");
        when(completeDTO.getPrescriptionInstructions()).thenReturn("  Take once daily  ");
        when(completeDTO.getSickLeaveStartDate()).thenReturn(null);
        when(completeDTO.getSickLeaveEndDate()).thenReturn(null);
        when(completeDTO.getDiagnosis()).thenReturn("Flu");

        when(appointmentRepository.findByIdAndDoctor_User_KeycloakId(appointmentId, doctorKeycloakId))
                .thenReturn(Optional.of(appointment));

        Diagnosis existingDiagnosis = Diagnosis.builder().name("flu").build();
        when(diagnosisRepository.findByNameIgnoreCase("flu")).thenReturn(Optional.of(existingDiagnosis));

        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        appointmentService.completeAppointment(appointmentId, doctorKeycloakId, completeDTO);

        verify(prescriptionRepository).save(prescriptionCaptor.capture());
        Prescription savedPrescription = prescriptionCaptor.getValue();
        assertEquals(appointment, savedPrescription.getAppointment());
        assertEquals("Take once daily", savedPrescription.getInstructions());

        verify(appointmentRepository).save(appointmentCaptor.capture());
        Appointment savedAppointment = appointmentCaptor.getValue();
        assertEquals("Doctor notes", savedAppointment.getDoctorNotes());
        assertEquals(existingDiagnosis, savedAppointment.getDiagnosis());
        assertTrue(savedAppointment.isCompleted());
        assertEquals("  Take once daily  ", savedAppointment.getPrescriptionInstructions());
    }

    @Test
    void completeAppointment_shouldThrowIllegalArgumentException_whenSickLeaveHasOnlyOneDate() {

        Long appointmentId = 404L;
        Appointment appointment = buildAppointmentForDoctor(appointmentId, LocalDate.now().atTime(10, 0));
        AppointmentCompleteDTO completeDTO = mock(AppointmentCompleteDTO.class);

        when(completeDTO.getDoctorNotes()).thenReturn("Doctor notes");
        when(completeDTO.getPrescriptionInstructions()).thenReturn(null);
        when(completeDTO.getSickLeaveStartDate()).thenReturn(LocalDate.now());
        when(completeDTO.getSickLeaveEndDate()).thenReturn(null);

        when(appointmentRepository.findByIdAndDoctor_User_KeycloakId(appointmentId, doctorKeycloakId))
                .thenReturn(Optional.of(appointment));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> appointmentService.completeAppointment(appointmentId, doctorKeycloakId, completeDTO)
        );

        assertEquals("Sick leave requires both start and end date.", exception.getMessage());

        verify(sickLeaveRepository, never()).save(any());
        verify(diagnosisRepository, never()).findByNameIgnoreCase(anyString());
        verify(diagnosisRepository, never()).save(any());
        verify(appointmentRepository, never()).save(any());
        verify(prescriptionRepository, never()).save(any());
    }


    @Test
    void completeAppointment_shouldUpsertSickLeave_whenSickLeaveDatesProvided() {
        Long appointmentId = 405L;
        Appointment appointment = buildAppointmentForDoctor(appointmentId, LocalDate.now().atTime(10, 0));
        AppointmentCompleteDTO completeDTO = mock(AppointmentCompleteDTO.class);

        LocalDate sickLeaveStartDate = LocalDate.now();
        LocalDate sickLeaveEndDate = sickLeaveStartDate.plusDays(3);

        when(completeDTO.getDoctorNotes()).thenReturn("Doctor notes");
        when(completeDTO.getPrescriptionInstructions()).thenReturn(null);
        when(completeDTO.getSickLeaveStartDate()).thenReturn(sickLeaveStartDate);
        when(completeDTO.getSickLeaveEndDate()).thenReturn(sickLeaveEndDate);
        when(completeDTO.getDiagnosis()).thenReturn("Flu");

        when(appointmentRepository.findByIdAndDoctor_User_KeycloakId(appointmentId, doctorKeycloakId))
                .thenReturn(Optional.of(appointment));

        when(sickLeaveRepository.findByAppointment_Id(appointmentId)).thenReturn(Optional.empty());

        Diagnosis existingDiagnosis = Diagnosis.builder().name("flu").build();
        when(diagnosisRepository.findByNameIgnoreCase("flu")).thenReturn(Optional.of(existingDiagnosis));

        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        appointmentService.completeAppointment(appointmentId, doctorKeycloakId, completeDTO);

        verify(sickLeaveRepository).save(sickLeaveCaptor.capture());
        SickLeave savedSickLeave = sickLeaveCaptor.getValue();
        assertEquals(appointment, savedSickLeave.getAppointment());
        assertEquals(sickLeaveStartDate, savedSickLeave.getStartDate());
        assertEquals(sickLeaveEndDate, savedSickLeave.getEndDate());

        verify(appointmentRepository).save(appointmentCaptor.capture());
        Appointment savedAppointment = appointmentCaptor.getValue();
        assertEquals(existingDiagnosis, savedAppointment.getDiagnosis());
        assertTrue(savedAppointment.isCompleted());
    }

    @Test
    void completeAppointment_shouldCreateDiagnosis_whenNotExisting() {
        Long appointmentId = 406L;
        Appointment appointment = buildAppointmentForDoctor(appointmentId, LocalDate.now().atTime(10, 0));
        AppointmentCompleteDTO completeDTO = mock(AppointmentCompleteDTO.class);

        when(completeDTO.getDoctorNotes()).thenReturn("Doctor notes");
        when(completeDTO.getPrescriptionInstructions()).thenReturn(null);
        when(completeDTO.getSickLeaveStartDate()).thenReturn(null);
        when(completeDTO.getSickLeaveEndDate()).thenReturn(null);
        when(completeDTO.getDiagnosis()).thenReturn("  Influenza  ");

        when(appointmentRepository.findByIdAndDoctor_User_KeycloakId(appointmentId, doctorKeycloakId))
                .thenReturn(Optional.of(appointment));

        when(diagnosisRepository.findByNameIgnoreCase("influenza")).thenReturn(Optional.empty());

        Diagnosis savedDiagnosis = Diagnosis.builder().name("influenza").build();
        when(diagnosisRepository.save(any(Diagnosis.class))).thenReturn(savedDiagnosis);

        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        appointmentService.completeAppointment(appointmentId, doctorKeycloakId, completeDTO);

        verify(diagnosisRepository).save(diagnosisCaptor.capture());
        Diagnosis diagnosisToSave = diagnosisCaptor.getValue();
        assertEquals("influenza", diagnosisToSave.getName());

        verify(appointmentRepository).save(appointmentCaptor.capture());
        Appointment savedAppointment = appointmentCaptor.getValue();
        assertEquals(savedDiagnosis, savedAppointment.getDiagnosis());
        assertTrue(savedAppointment.isCompleted());
    }

    private Patient buildPatient(Long patientId) {
        Patient patient = new Patient();
        patient.setId(patientId);
        patient.setFirstName("PatientFirstName");
        patient.setLastName("PatientLastName");
        patient.setEgn("1234567890");
        return patient;
    }

    private Doctor buildDoctor(Long doctorId) {
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setMedicalId("MED-001");
        doctor.setFirstName("DoctorFirstName");
        doctor.setLastName("DoctorLastName");
        doctor.setSpecialty("Cardiology");
        return doctor;
    }

    private Appointment buildAppointmentForDoctor(Long appointmentId, LocalDateTime visitDateTime) {
        Appointment appointment = Appointment.builder()
                .visitDate(visitDateTime)
                .patient(buildPatient(1L))
                .doctor(buildDoctor(2L))
                .notes("Initial notes")
                .completed(false)
                .build();
        appointment.setId(appointmentId);
        return appointment;
    }
}
