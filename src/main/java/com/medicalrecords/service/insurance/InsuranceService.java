package com.medicalrecords.service.insurance;

public interface InsuranceService {

    boolean isPaidLastSixMonths(Long patientId);
}