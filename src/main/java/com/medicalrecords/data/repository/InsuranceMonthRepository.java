package com.medicalrecords.data.repository;

import com.medicalrecords.data.entity.InsuranceMonth;
import com.medicalrecords.data.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InsuranceMonthRepository extends JpaRepository<InsuranceMonth, Long> {

    List<InsuranceMonth> findByPatientAndMonthValueBetweenOrderByMonthValueAsc(
            Patient patient,
            LocalDate startInclusive,
            LocalDate endInclusive
    );

    Optional<InsuranceMonth> findByPatientAndMonthValue(Patient patient, LocalDate monthValue);
}