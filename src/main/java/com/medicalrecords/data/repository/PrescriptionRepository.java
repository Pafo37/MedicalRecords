package com.medicalrecords.data.repository;

import com.medicalrecords.data.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findAllByAppointment_IdOrderByIdDesc(Long appointmentId);
}
