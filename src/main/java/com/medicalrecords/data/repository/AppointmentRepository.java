package com.medicalrecords.data.repository;

import com.medicalrecords.data.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findAllByDoctor_User_KeycloakIdOrderByVisitDateAsc(String doctorKeycloakId);

    Optional<Appointment> findByIdAndDoctor_User_KeycloakId(Long appointmentId, String doctorKeycloakId);

    List<Appointment> findAllByPatient_User_KeycloakIdOrderByVisitDateAsc(String patientKeycloakId);

    Optional<Appointment> findByIdAndPatient_User_KeycloakId(Long appointmentId, String patientKeycloakId);

    List<Appointment> findAllByPatient_User_KeycloakIdAndCompletedTrueAndDiagnosisIsNotNullOrderByVisitDateDesc(String patientKeycloakId);

    List<Appointment> findAllByPatient_IdAndCompletedTrueOrderByVisitDateDesc(Long patientId);

    boolean existsByDoctor_User_KeycloakIdAndPatient_Id(String doctorKeycloakId, Long patientId);

    @Query(value = """
                SELECT
                    diagnoses.name AS diagnosis_name,
                    COUNT(DISTINCT appointments.patient_id) AS distinct_patient_count
                FROM appointments
                INNER JOIN diagnoses
                    ON diagnoses.id = appointments.diagnosis_id
                WHERE appointments.diagnosis_id IS NOT NULL
                  AND appointments.completed = TRUE
                GROUP BY diagnoses.name
                ORDER BY distinct_patient_count DESC, diagnosis_name ASC
            """, nativeQuery = true)
    List<Object[]> findDistinctPatientCountsGroupedByDiagnosisName();


    @Query(value = """
                SELECT
                    doctors.id AS doctor_id,
                    doctors.medical_id AS doctor_medical_id,
                    doctors.first_name AS doctor_first_name,
                    doctors.last_name AS doctor_last_name,
                    COUNT(appointments.id) AS total_appointment_count,
                    SUM(CASE WHEN appointments.completed = TRUE THEN 1 ELSE 0 END) AS completed_appointment_count
                FROM appointments
                INNER JOIN doctors
                    ON doctors.id = appointments.doctor_id
                GROUP BY doctors.id, doctors.medical_id, doctors.first_name, doctors.last_name
                ORDER BY total_appointment_count DESC, doctors.last_name ASC, doctors.first_name ASC
            """, nativeQuery = true)
    List<Object[]> findAppointmentCountsGroupedByDoctor();

}