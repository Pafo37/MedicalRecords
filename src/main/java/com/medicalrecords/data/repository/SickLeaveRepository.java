package com.medicalrecords.data.repository;

import com.medicalrecords.data.entity.SickLeave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SickLeaveRepository extends JpaRepository<SickLeave, Long> {

    Optional<SickLeave> findByAppointmentId(Long appointmentId);

    List<SickLeave> findAllByPatientIdOrderByStartDateDesc(Long patientId);

    List<SickLeave> findAllByDoctorIdOrderByStartDateDesc(Long doctorId);

    List<SickLeave> findAllByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate date1, LocalDate date2);
}