package com.medicalrecords.service.insurance;

import com.medicalrecords.data.entity.InsuranceMonth;
import com.medicalrecords.data.entity.Patient;
import com.medicalrecords.data.repository.InsuranceMonthRepository;
import com.medicalrecords.data.repository.PatientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class InsuranceServiceImpl implements InsuranceService {

    private final InsuranceMonthRepository insuranceMonthRepository;
    private final PatientRepository patientRepository;

    public List<InsuranceMonth> getLastSixMonthsForPatient(Patient patient) {

        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(5);

        LocalDate startDate = startMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        List<InsuranceMonth> existingRows =
                insuranceMonthRepository.findByPatientAndMonthValueBetweenOrderByMonthValueAsc(
                        patient, startDate, endDate
                );

        Map<YearMonth, InsuranceMonth> byMonth = new HashMap<>();
        for (InsuranceMonth row : existingRows) {
            byMonth.put(row.getMonth(), row);
        }

        for (int i = 0; i < 6; i++) {
            YearMonth month = startMonth.plusMonths(i);
            if (!byMonth.containsKey(month)) {
                InsuranceMonth newRow = new InsuranceMonth();
                newRow.setPatient(patient);
                newRow.setMonth(month);
                newRow.setPaid(false);
                InsuranceMonth saved = insuranceMonthRepository.save(newRow);
                byMonth.put(month, saved);
            }
        }

        List<InsuranceMonth> lastSixMonths =
                insuranceMonthRepository.findByPatientAndMonthValueBetweenOrderByMonthValueAsc(
                        patient, startDate, endDate
                );

        updatePatientPaidLastSixMonthsFlag(patient, lastSixMonths);
        return lastSixMonths;
    }

    public void payMonth(Patient patient, YearMonth monthToPay) {

        LocalDate monthValue = monthToPay.atDay(1);

        InsuranceMonth row = insuranceMonthRepository
                .findByPatientAndMonthValue(patient, monthValue)
                .orElseGet(() -> {
                    InsuranceMonth newRow = new InsuranceMonth();
                    newRow.setPatient(patient);
                    newRow.setMonth(monthToPay);
                    newRow.setPaid(false);
                    return insuranceMonthRepository.save(newRow);
                });

        if (!row.isPaid()) {
            row.setPaid(true);
            row.setPaidAt(LocalDate.now());
            insuranceMonthRepository.save(row);
        }

        getLastSixMonthsForPatient(patient);
    }

    private void updatePatientPaidLastSixMonthsFlag(Patient patient, List<InsuranceMonth> lastSixMonths) {
        boolean allPaid = lastSixMonths.stream().allMatch(InsuranceMonth::isPaid);
        patient.setInsurancePaidLast6Months(allPaid);
        patientRepository.save(patient);
    }

}
