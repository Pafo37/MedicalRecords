package com.medicalrecords.data.repository;

import com.medicalrecords.data.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByMedicalId(String medicalId);

}
