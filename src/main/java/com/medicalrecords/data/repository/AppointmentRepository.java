package com.medicalrecords.data.repository;

import com.medicalrecords.data.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findAllByPatient_IdOrderByVisitDateDesc(Long patientId);

    List<Appointment> findAllByDoctor_IdOrderByVisitDateDesc(Long doctorId);

}