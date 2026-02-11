package com.medicalrecords.data.repository;

import com.medicalrecords.data.entity.Patient;
import com.medicalrecords.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByEgn(String egn);

    Optional<Patient> findByUser(User user);

    Optional<Patient> findByUser_KeycloakId(String keycloakId);

    Optional<Patient> findById(Long id);

    @Query(value = """
                SELECT
                    doctors.id AS doctor_id,
                    doctors.medical_id AS doctor_medical_id,
                    doctors.first_name AS doctor_first_name,
                    doctors.last_name AS doctor_last_name,
                    COUNT(patients.id) AS primary_patient_count
                FROM patients
                INNER JOIN doctors
                    ON doctors.id = patients.primary_care_doctor_id
                GROUP BY doctors.id, doctors.medical_id, doctors.first_name, doctors.last_name
                ORDER BY primary_patient_count DESC, doctors.last_name ASC, doctors.first_name ASC
            """, nativeQuery = true)
    List<Object[]> findPrimaryPatientCountsGroupedByDoctor();

}