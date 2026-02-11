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
}