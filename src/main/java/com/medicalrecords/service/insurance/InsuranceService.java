package com.medicalrecords.service.insurance;

import com.medicalrecords.data.entity.InsuranceMonth;
import com.medicalrecords.data.entity.Patient;

import java.time.YearMonth;
import java.util.List;

public interface InsuranceService {

    List<InsuranceMonth> getLastSixMonthsForPatient(Patient patient);

    void payMonth(Patient patient, YearMonth monthToPay);
}