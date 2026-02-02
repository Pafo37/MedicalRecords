package com.medicalrecords.service.insurance;

import com.medicalrecords.data.repository.HealthInsuranceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class InsuranceServiceImpl implements InsuranceService {

    private final HealthInsuranceRepository healthInsuranceRepository;

    @Override
    public boolean isPaidLastSixMonths(Long patientId) {
        LocalDate now = LocalDate.now();
        LocalDate endMonth = now.withDayOfMonth(1);
        LocalDate startMonth = endMonth.minusMonths(5);

        long paidCount = healthInsuranceRepository
                .countByPatientIdAndMonthBetweenAndPaidIsTrue(patientId, startMonth, endMonth);

        return paidCount == 6;
    }
}
