package com.medicalrecords.repository;

import com.medicalrecords.data.entity.Doctor;
import com.medicalrecords.data.entity.Patient;
import com.medicalrecords.data.entity.User;
import com.medicalrecords.data.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PatientRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void findByEgn_shouldReturnPatient_whenEgnExists() {
        User patientUser = persistUser("patient-kc-1", "patient1", "ROLE_PATIENT");
        Patient patient = persistPatient(patientUser, "Maria", "Petrova", "1234567890", null);

        testEntityManager.flush();
        testEntityManager.clear();

        Optional<Patient> result = patientRepository.findByEgn("1234567890");

        assertTrue(result.isPresent());
        assertEquals(patient.getId(), result.get().getId());
        assertEquals("Maria", result.get().getFirstName());
        assertEquals("Petrova", result.get().getLastName());
        assertEquals("1234567890", result.get().getEgn());
    }

    @Test
    void findByEgn_shouldReturnEmpty_whenEgnDoesNotExist() {
        Optional<Patient> result = patientRepository.findByEgn("0000000000");
        assertTrue(result.isEmpty());
    }

    @Test
    void findByUser_shouldReturnPatient_whenUserExists() {
        User patientUser = persistUser("patient-kc-2", "patient2", "ROLE_PATIENT");
        Patient patient = persistPatient(patientUser, "Petar", "Petrov", "0987654321", null);

        testEntityManager.flush();
        testEntityManager.clear();

        Optional<Patient> result = patientRepository.findByUser(patientUser);

        assertTrue(result.isPresent());
        assertEquals(patient.getId(), result.get().getId());
        assertEquals("0987654321", result.get().getEgn());
    }

    @Test
    void findByUser_KeycloakId_shouldReturnPatient_whenKeycloakIdExists() {
        User patientUser = persistUser("patient-kc-3", "patient3", "ROLE_PATIENT");
        Patient patient = persistPatient(patientUser, "Elena", "Nikolova", "1111111111", null);

        testEntityManager.flush();
        testEntityManager.clear();

        Optional<Patient> result = patientRepository.findByUser_KeycloakId("patient-kc-3");

        assertTrue(result.isPresent());
        assertEquals(patient.getId(), result.get().getId());
        assertEquals("patient-kc-3", result.get().getUser().getKeycloakId());
    }

    @Test
    void findPrimaryPatientCountsGroupedByDoctor_shouldReturnCountsAndOrdering() {
        User doctorUserOne = persistUser("doctor-kc-1", "doctor1", "ROLE_DOCTOR");
        Doctor doctorOne = persistDoctor(doctorUserOne, "MED-001", "Anna", "Angelova", "ENT");

        User doctorUserTwo = persistUser("doctor-kc-2", "doctor2", "ROLE_DOCTOR");
        Doctor doctorTwo = persistDoctor(doctorUserTwo, "MED-002", "Boris", "Borisov", "ENT");

        User patientUserOne = persistUser("patient-kc-4", "patient4", "ROLE_PATIENT");
        User patientUserTwo = persistUser("patient-kc-5", "patient5", "ROLE_PATIENT");
        User patientUserThree = persistUser("patient-kc-6", "patient6", "ROLE_PATIENT");

        persistPatient(patientUserOne, "Patient", "One", "2222222222", doctorTwo);
        persistPatient(patientUserTwo, "Patient", "Two", "3333333333", doctorTwo);
        persistPatient(patientUserThree, "Patient", "Three", "4444444444", doctorOne);

        testEntityManager.flush();
        testEntityManager.clear();

        List<Object[]> results = patientRepository.findPrimaryPatientCountsGroupedByDoctor();

        assertEquals(2, results.size());

        Object[] firstRow = results.get(0);
        Long firstDoctorId = ((Number) firstRow[0]).longValue();
        String firstDoctorMedicalId = (String) firstRow[1];
        String firstDoctorFirstName = (String) firstRow[2];
        String firstDoctorLastName = (String) firstRow[3];
        Long firstPrimaryPatientCount = ((Number) firstRow[4]).longValue();

        assertEquals(doctorTwo.getId(), firstDoctorId);
        assertEquals("MED-002", firstDoctorMedicalId);
        assertEquals("Boris", firstDoctorFirstName);
        assertEquals("Borisov", firstDoctorLastName);
        assertEquals(2L, firstPrimaryPatientCount);

        Object[] secondRow = results.get(1);
        Long secondDoctorId = ((Number) secondRow[0]).longValue();
        String secondDoctorMedicalId = (String) secondRow[1];
        String secondDoctorFirstName = (String) secondRow[2];
        String secondDoctorLastName = (String) secondRow[3];
        Long secondPrimaryPatientCount = ((Number) secondRow[4]).longValue();

        assertEquals(doctorOne.getId(), secondDoctorId);
        assertEquals("MED-001", secondDoctorMedicalId);
        assertEquals("Anna", secondDoctorFirstName);
        assertEquals("Angelova", secondDoctorLastName);
        assertEquals(1L, secondPrimaryPatientCount);
    }

    private User persistUser(String keycloakId, String username, String role) {
        User user = new User();
        user.setKeycloakId(keycloakId);
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setRole(role);
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

    private Patient persistPatient(User user, String firstName, String lastName, String egn, Doctor primaryCareDoctor) {
        Patient patient = new Patient();
        patient.setUser(user);
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setEgn(egn);
        patient.setPrimaryCareDoctor(primaryCareDoctor);
        patient.setInsurancePaidLast6Months(false);
        return testEntityManager.persistAndFlush(patient);
    }
}