package com.medicalrecords.data.repository;

import com.medicalrecords.data.entity.HealthInsurance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HealthInsuranceRepository extends JpaRepository<HealthInsurance, Long> {

    List<HealthInsurance> findAllByPatientIdAndMonthBetween(Long patientId, LocalDate from, LocalDate to);

    long countByPatientIdAndMonthBetweenAndPaidIsTrue(Long patientId, LocalDate from, LocalDate to);
}