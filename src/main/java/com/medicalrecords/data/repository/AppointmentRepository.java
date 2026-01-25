package com.medicalrecords.data.repository;

import com.medicalrecords.data.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findAllByPatientIdOrderByAppointmentDateDesc(Long patientId);

    List<Appointment> findAllByDoctorIdOrderByAppointmentDateDesc(Long doctorId);

    List<Appointment> findAllByPatientIdAndAppointmentDateBetweenOrderByAppointmentDateDesc(
            Long patientId, LocalDate from, LocalDate to
    );
}