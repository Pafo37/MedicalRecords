package com.medicalrecords.repository;

import com.medicalrecords.data.entity.*;
import com.medicalrecords.data.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AppointmentRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Test
    void findAllByDoctor_User_KeycloakIdOrderByVisitDateAsc_shouldReturnDoctorAppointmentsOrderedByVisitDateAscending() {
        User doctorUser = persistUser("doctor-kc-1", "doctor1");
        Doctor doctor = persistDoctor(doctorUser, "MED-001", "Ivan", "Ivanov", "Cardiology");

        User patientUser = persistUser("patient-kc-1", "patient1");
        Patient patient = persistPatient(patientUser, "Maria", "Petrova", "1234567890");

        LocalDateTime earlierVisitDate = LocalDateTime.now().minusDays(1).withSecond(0).withNano(0);
        LocalDateTime laterVisitDate = LocalDateTime.now().plusDays(1).withSecond(0).withNano(0);

        Appointment laterAppointment = persistAppointment(patient, doctor, laterVisitDate, "Later", true, null);
        Appointment earlierAppointment = persistAppointment(patient, doctor, earlierVisitDate, "Earlier", true, null);

        testEntityManager.flush();
        testEntityManager.clear();

        List<Appointment> appointments =
                appointmentRepository.findAllByDoctor_User_KeycloakIdOrderByVisitDateAsc("doctor-kc-1");

        assertEquals(2, appointments.size());
        assertEquals(earlierAppointment.getId(), appointments.get(0).getId());
        assertEquals(laterAppointment.getId(), appointments.get(1).getId());
    }

    @Test
    void findByIdAndDoctor_User_KeycloakId_shouldReturnAppointmentOnlyWhenDoctorMatches() {
        User doctorUser = persistUser("doctor-kc-2", "doctor2");
        Doctor doctor = persistDoctor(doctorUser, "MED-002", "Georgi", "Georgiev", "Dermatology");

        User otherDoctorUser = persistUser("doctor-kc-3", "doctor3");
        Doctor otherDoctor = persistDoctor(otherDoctorUser, "MED-003", "Anna", "Ivanova", "Neurology");

        User patientUser = persistUser("patient-kc-2", "patient2");
        Patient patient = persistPatient(patientUser, "Petar", "Petrov", "0987654321");

        Appointment appointment = persistAppointment(patient, doctor, LocalDateTime.now().withSecond(0).withNano(0), "Notes", false, null);

        testEntityManager.flush();
        testEntityManager.clear();

        Optional<Appointment> matchingDoctorResult =
                appointmentRepository.findByIdAndDoctor_User_KeycloakId(appointment.getId(), "doctor-kc-2");

        Optional<Appointment> nonMatchingDoctorResult =
                appointmentRepository.findByIdAndDoctor_User_KeycloakId(appointment.getId(), "doctor-kc-3");

        assertTrue(matchingDoctorResult.isPresent());
        assertEquals(appointment.getId(), matchingDoctorResult.get().getId());

        assertTrue(nonMatchingDoctorResult.isEmpty());
    }

    @Test
    void findAllByPatient_User_KeycloakIdOrderByVisitDateAsc_shouldReturnPatientAppointmentsOrderedByVisitDateAscending() {
        User doctorUser = persistUser("doctor-kc-4", "doctor4");
        Doctor doctor = persistDoctor(doctorUser, "MED-004", "Nikolay", "Dimitrov", "Orthopedics");

        User patientUser = persistUser("patient-kc-3", "patient3");
        Patient patient = persistPatient(patientUser, "Elena", "Nikolova", "1111111111");

        LocalDateTime firstVisitDate = LocalDateTime.now().minusDays(2).withSecond(0).withNano(0);
        LocalDateTime secondVisitDate = LocalDateTime.now().minusDays(1).withSecond(0).withNano(0);

        Appointment secondAppointment = persistAppointment(patient, doctor, secondVisitDate, "Second", false, null);
        Appointment firstAppointment = persistAppointment(patient, doctor, firstVisitDate, "First", false, null);

        testEntityManager.flush();
        testEntityManager.clear();

        List<Appointment> appointments =
                appointmentRepository.findAllByPatient_User_KeycloakIdOrderByVisitDateAsc("patient-kc-3");

        assertEquals(2, appointments.size());
        assertEquals(firstAppointment.getId(), appointments.get(0).getId());
        assertEquals(secondAppointment.getId(), appointments.get(1).getId());
    }

    @Test
    void findByIdAndPatient_User_KeycloakId_shouldReturnAppointmentOnlyWhenPatientMatches() {
        User doctorUser = persistUser("doctor-kc-5", "doctor5");
        Doctor doctor = persistDoctor(doctorUser, "MED-005", "Kalin", "Kolev", "Pediatrics");

        User patientUser = persistUser("patient-kc-4", "patient4");
        Patient patient = persistPatient(patientUser, "Dimitar", "Dimitrov", "2222222222");

        User otherPatientUser = persistUser("patient-kc-5", "patient5");
        Patient otherPatient = persistPatient(otherPatientUser, "Viktoria", "Vasileva", "3333333333");

        Appointment appointment = persistAppointment(patient, doctor, LocalDateTime.now().withSecond(0).withNano(0), "Notes", false, null);

        testEntityManager.flush();
        testEntityManager.clear();

        Optional<Appointment> matchingPatientResult =
                appointmentRepository.findByIdAndPatient_User_KeycloakId(appointment.getId(), "patient-kc-4");

        Optional<Appointment> nonMatchingPatientResult =
                appointmentRepository.findByIdAndPatient_User_KeycloakId(appointment.getId(), "patient-kc-5");

        assertTrue(matchingPatientResult.isPresent());
        assertEquals(appointment.getId(), matchingPatientResult.get().getId());

        assertTrue(nonMatchingPatientResult.isEmpty());
    }

    @Test
    void findAllByPatient_User_KeycloakIdAndCompletedTrueAndDiagnosisIsNotNullOrderByVisitDateDesc_shouldReturnOnlyCompletedWithDiagnosisOrderedDesc() {
        User doctorUser = persistUser("doctor-kc-6", "doctor6");
        Doctor doctor = persistDoctor(doctorUser, "MED-006", "Stoyan", "Stoyanov", "Internal Medicine");

        User patientUser = persistUser("patient-kc-6", "patient6");
        Patient patient = persistPatient(patientUser, "Radoslav", "Radev", "4444444444");

        Diagnosis fluDiagnosis = persistDiagnosis("flu");
        Diagnosis coldDiagnosis = persistDiagnosis("cold");

        LocalDateTime olderVisitDate = LocalDateTime.now().minusDays(10).withSecond(0).withNano(0);
        LocalDateTime newerVisitDate = LocalDateTime.now().minusDays(5).withSecond(0).withNano(0);

        Appointment completedWithDiagnosisOlder = persistAppointment(patient, doctor, olderVisitDate, "Older", true, fluDiagnosis);
        Appointment completedWithDiagnosisNewer = persistAppointment(patient, doctor, newerVisitDate, "Newer", true, coldDiagnosis);

        persistAppointment(patient, doctor, LocalDateTime.now().minusDays(1).withSecond(0).withNano(0), "Not completed", false, coldDiagnosis);
        persistAppointment(patient, doctor, LocalDateTime.now().minusDays(2).withSecond(0).withNano(0), "No diagnosis", true, null);

        testEntityManager.flush();
        testEntityManager.clear();

        List<Appointment> appointments =
                appointmentRepository.findAllByPatient_User_KeycloakIdAndCompletedTrueAndDiagnosisIsNotNullOrderByVisitDateDesc("patient-kc-6");

        assertEquals(2, appointments.size());
        assertEquals(completedWithDiagnosisNewer.getId(), appointments.get(0).getId());
        assertEquals(completedWithDiagnosisOlder.getId(), appointments.get(1).getId());
    }

    @Test
    void findAllByPatient_IdAndCompletedTrueOrderByVisitDateDesc_shouldReturnOnlyCompletedAppointmentsOrderedDesc() {
        User doctorUser = persistUser("doctor-kc-7", "doctor7");
        Doctor doctor = persistDoctor(doctorUser, "MED-007", "Milen", "Milev", "Urology");

        User patientUser = persistUser("patient-kc-7", "patient7");
        Patient patient = persistPatient(patientUser, "Hristo", "Hristov", "5555555555");

        LocalDateTime olderVisitDate = LocalDateTime.now().minusDays(7).withSecond(0).withNano(0);
        LocalDateTime newerVisitDate = LocalDateTime.now().minusDays(3).withSecond(0).withNano(0);

        Appointment completedOlder = persistAppointment(patient, doctor, olderVisitDate, "Older", true, null);
        Appointment completedNewer = persistAppointment(patient, doctor, newerVisitDate, "Newer", true, null);

        persistAppointment(patient, doctor, LocalDateTime.now().minusDays(1).withSecond(0).withNano(0), "Not completed", false, null);

        testEntityManager.flush();
        testEntityManager.clear();

        List<Appointment> appointments =
                appointmentRepository.findAllByPatient_IdAndCompletedTrueOrderByVisitDateDesc(patient.getId());

        assertEquals(2, appointments.size());
        assertEquals(completedNewer.getId(), appointments.get(0).getId());
        assertEquals(completedOlder.getId(), appointments.get(1).getId());
    }

    @Test
    void existsByDoctor_User_KeycloakIdAndPatient_Id_shouldReturnTrueOnlyWhenDoctorHasAnyAppointmentWithPatient() {
        User doctorUser = persistUser("doctor-kc-8", "doctor8");
        Doctor doctor = persistDoctor(doctorUser, "MED-008", "Boris", "Borisov", "Surgery");

        User otherDoctorUser = persistUser("doctor-kc-9", "doctor9");
        Doctor otherDoctor = persistDoctor(otherDoctorUser, "MED-009", "Violeta", "Vasileva", "Surgery");

        User patientUser = persistUser("patient-kc-8", "patient8");
        Patient patient = persistPatient(patientUser, "Aleksandar", "Aleksandrov", "6666666666");

        persistAppointment(patient, doctor, LocalDateTime.now().withSecond(0).withNano(0), "Visit", false, null);

        testEntityManager.flush();
        testEntityManager.clear();

        boolean doctorHasAccess = appointmentRepository.existsByDoctor_User_KeycloakIdAndPatient_Id("doctor-kc-8", patient.getId());
        boolean otherDoctorHasAccess = appointmentRepository.existsByDoctor_User_KeycloakIdAndPatient_Id("doctor-kc-9", patient.getId());

        assertTrue(doctorHasAccess);
        assertFalse(otherDoctorHasAccess);
    }

    @Test
    void findDistinctPatientCountsGroupedByDiagnosisName_shouldCountDistinctPatientsOnlyForCompletedAppointmentsWithDiagnosis() {
        User doctorUser = persistUser("doctor-kc-10", "doctor10");
        Doctor doctor = persistDoctor(doctorUser, "MED-010", "Teodor", "Todorov", "General Practice");

        Diagnosis fluDiagnosis = persistDiagnosis("flu");
        Diagnosis coldDiagnosis = persistDiagnosis("cold");

        User patientUserOne = persistUser("patient-kc-9", "patient9");
        Patient patientOne = persistPatient(patientUserOne, "Patient", "One", "7777777777");

        User patientUserTwo = persistUser("patient-kc-10", "patient10");
        Patient patientTwo = persistPatient(patientUserTwo, "Patient", "Two", "8888888888");

        persistAppointment(patientOne, doctor, LocalDateTime.now().minusDays(5).withSecond(0).withNano(0), "Completed flu 1", true, fluDiagnosis);
        persistAppointment(patientOne, doctor, LocalDateTime.now().minusDays(4).withSecond(0).withNano(0), "Completed flu 2", true, fluDiagnosis);
        persistAppointment(patientTwo, doctor, LocalDateTime.now().minusDays(3).withSecond(0).withNano(0), "Completed flu 3", true, fluDiagnosis);

        persistAppointment(patientOne, doctor, LocalDateTime.now().minusDays(2).withSecond(0).withNano(0), "Completed cold", true, coldDiagnosis);

        persistAppointment(patientTwo, doctor, LocalDateTime.now().minusDays(1).withSecond(0).withNano(0), "Not completed flu", false, fluDiagnosis);
        persistAppointment(patientTwo, doctor, LocalDateTime.now().minusDays(1).withSecond(0).withNano(0), "Completed no diagnosis", true, null);

        testEntityManager.flush();
        testEntityManager.clear();

        List<Object[]> results = appointmentRepository.findDistinctPatientCountsGroupedByDiagnosisName();

        assertEquals(2, results.size());

        Object[] firstRow = results.get(0);
        String firstDiagnosisName = (String) firstRow[0];
        Long firstDistinctPatientCount = ((Number) firstRow[1]).longValue();

        Object[] secondRow = results.get(1);
        String secondDiagnosisName = (String) secondRow[0];
        Long secondDistinctPatientCount = ((Number) secondRow[1]).longValue();

        assertEquals("flu", firstDiagnosisName);
        assertEquals(2L, firstDistinctPatientCount);

        assertEquals("cold", secondDiagnosisName);
        assertEquals(1L, secondDistinctPatientCount);
    }

    @Test
    void findAppointmentCountsGroupedByDoctor_shouldReturnTotalAndCompletedCountsAndOrdering() {
        User doctorUserOne = persistUser("doctor-kc-11", "doctor11");
        Doctor doctorOne = persistDoctor(doctorUserOne, "MED-011", "Anna", "Angelova", "ENT");

        User doctorUserTwo = persistUser("doctor-kc-12", "doctor12");
        Doctor doctorTwo = persistDoctor(doctorUserTwo, "MED-012", "Boris", "Borisov", "ENT");

        User patientUser = persistUser("patient-kc-11", "patient11");
        Patient patient = persistPatient(patientUser, "Simeon", "Simeonov", "9999999999");

        persistAppointment(patient, doctorOne, LocalDateTime.now().minusDays(3).withSecond(0).withNano(0), "A1", true, null);
        persistAppointment(patient, doctorOne, LocalDateTime.now().minusDays(2).withSecond(0).withNano(0), "A2", false, null);

        persistAppointment(patient, doctorTwo, LocalDateTime.now().minusDays(1).withSecond(0).withNano(0), "B1", true, null);
        persistAppointment(patient, doctorTwo, LocalDateTime.now().minusHours(10).withSecond(0).withNano(0), "B2", true, null);
        persistAppointment(patient, doctorTwo, LocalDateTime.now().minusHours(5).withSecond(0).withNano(0), "B3", false, null);

        testEntityManager.flush();
        testEntityManager.clear();

        List<Object[]> results = appointmentRepository.findAppointmentCountsGroupedByDoctor();

        assertEquals(2, results.size());

        Object[] firstRow = results.get(0);
        Long firstDoctorId = ((Number) firstRow[0]).longValue();
        String firstDoctorMedicalId = (String) firstRow[1];
        String firstDoctorFirstName = (String) firstRow[2];
        String firstDoctorLastName = (String) firstRow[3];
        Long firstTotalAppointments = ((Number) firstRow[4]).longValue();
        Long firstCompletedAppointments = ((Number) firstRow[5]).longValue();

        assertEquals(doctorTwo.getId(), firstDoctorId);
        assertEquals("MED-012", firstDoctorMedicalId);
        assertEquals("Boris", firstDoctorFirstName);
        assertEquals("Borisov", firstDoctorLastName);
        assertEquals(3L, firstTotalAppointments);
        assertEquals(2L, firstCompletedAppointments);

        Object[] secondRow = results.get(1);
        Long secondDoctorId = ((Number) secondRow[0]).longValue();
        String secondDoctorMedicalId = (String) secondRow[1];
        String secondDoctorFirstName = (String) secondRow[2];
        String secondDoctorLastName = (String) secondRow[3];
        Long secondTotalAppointments = ((Number) secondRow[4]).longValue();
        Long secondCompletedAppointments = ((Number) secondRow[5]).longValue();

        assertEquals(doctorOne.getId(), secondDoctorId);
        assertEquals("MED-011", secondDoctorMedicalId);
        assertEquals("Anna", secondDoctorFirstName);
        assertEquals("Angelova", secondDoctorLastName);
        assertEquals(2L, secondTotalAppointments);
        assertEquals(1L, secondCompletedAppointments);
    }

    private User persistUser(String keycloakId, String username) {
        User user = new User();
        user.setKeycloakId(keycloakId);
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setRole("ROLE_PATIENT");
        return testEntityManager.persistAndFlush(user);
    }

    private Doctor persistDoctor(User user, String medicalId, String firstName, String lastName, String specialty) {
        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setMedicalId(medicalId);
        doctor.setFirstName(firstName);
        doctor.setLastName(lastName);
        doctor.setSpecialty(specialty);
        doctor.setPersonalDoctor(false);
        return testEntityManager.persistAndFlush(doctor);
    }

    private Patient persistPatient(User user, String firstName, String lastName, String egn) {
        Patient patient = new Patient();
        patient.setUser(user);
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setEgn(egn);
        patient.setInsurancePaidLast6Months(false);
        return testEntityManager.persistAndFlush(patient);
    }

    private Diagnosis persistDiagnosis(String name) {
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setName(name);
        return testEntityManager.persistAndFlush(diagnosis);
    }

    private Appointment persistAppointment(Patient patient,
                                           Doctor doctor,
                                           LocalDateTime visitDate,
                                           String notes,
                                           boolean completed,
                                           Diagnosis diagnosis) {
        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .visitDate(visitDate)
                .notes(notes)
                .completed(completed)
                .diagnosis(diagnosis)
                .build();
        return testEntityManager.persistAndFlush(appointment);
    }

    private List<InsuranceMonth>PlaceholderNotNeeded() {
        return List.of();
    }
}
