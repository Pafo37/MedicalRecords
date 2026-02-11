package com.medicalrecords.data.repository;

import com.medicalrecords.data.entity.SickLeave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SickLeaveRepository extends JpaRepository<SickLeave, Long> {

    Optional<SickLeave> findByAppointment_Id(Long appointmentId);

    List<SickLeave> findAllByAppointment_Patient_User_KeycloakIdOrderByStartDateDesc(String patientKeycloakId);

}