package com.medicalrecords.data.repository;

import com.medicalrecords.data.entity.Patient;
import com.medicalrecords.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByEgn(String egn);

    @Query("""
            SELECT doctor.id,
                   doctor.medicalId,
                   doctor.firstName,
                   doctor.lastName,
                   COUNT(patient)
            FROM Patient patient
            JOIN patient.primaryCareDoctor doctor
            GROUP BY doctor.id,
                     doctor.medicalId,
                     doctor.firstName,
                     doctor.lastName
           """)
    List<Object[]> countPatientsPerPrimaryCareDoctor();

    boolean existsByEgn(String egn);

    Optional<Patient> findByUser(User user);

    Optional<Patient> findByUser_KeycloakId(String keycloakId);
}