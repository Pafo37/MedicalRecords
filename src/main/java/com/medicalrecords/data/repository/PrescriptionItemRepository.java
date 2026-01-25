package com.medicalrecords.data.repository;

import com.medicalrecords.data.entity.PrescriptionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, Long> {
    List<PrescriptionItem> findAllByAppointmentId(Long appointmentId);
}
