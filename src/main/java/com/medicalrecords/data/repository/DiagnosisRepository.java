package com.medicalrecords.data.repository;

import com.medicalrecords.data.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    Optional<Diagnosis> findByNameIgnoreCase(String name);
}
