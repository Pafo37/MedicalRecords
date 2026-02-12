package com.medicalrecords.service;

import com.medicalrecords.data.entity.InsuranceMonth;
import com.medicalrecords.data.entity.Patient;
import com.medicalrecords.data.repository.InsuranceMonthRepository;
import com.medicalrecords.data.repository.PatientRepository;
import com.medicalrecords.service.insurance.InsuranceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InsuranceServiceImplTest {

    @Mock
    private InsuranceMonthRepository insuranceMonthRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private InsuranceServiceImpl insuranceService;

    @Captor
    private ArgumentCaptor<InsuranceMonth> insuranceMonthCaptor;

    @Captor
    private ArgumentCaptor<Patient> patientCaptor;

    @Test
    void getLastSixMonthsForPatient_shouldCreateMissingRows_whenRepositoryReturnsEmpty() {
        Patient patient = buildPatient(1L);

        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(5);
        LocalDate startDate = startMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        when(insuranceMonthRepository.findByPatientAndMonthValueBetweenOrderByMonthValueAsc(patient, startDate, endDate))
                .thenReturn(List.of())
                .thenAnswer(invocation -> buildInsuranceRowsForRange(patient, startMonth, 6, false));

        when(insuranceMonthRepository.save(any(InsuranceMonth.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(patientRepository.save(any(Patient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<InsuranceMonth> result = insuranceService.getLastSixMonthsForPatient(patient);

        assertNotNull(result);
        assertEquals(6, result.size());

        verify(insuranceMonthRepository, times(6)).save(any(InsuranceMonth.class));
        verify(patientRepository).save(patientCaptor.capture());

        Patient savedPatient = patientCaptor.getValue();
        assertFalse(savedPatient.isInsurancePaidLast6Months());
    }

    @Test
    void getLastSixMonthsForPatient_shouldNotCreateRows_whenAllSixAlreadyExist() {
        Patient patient = buildPatient(2L);

        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(5);
        LocalDate startDate = startMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        List<InsuranceMonth> existingRows = buildInsuranceRowsForRange(patient, startMonth, 6, false);

        when(insuranceMonthRepository.findByPatientAndMonthValueBetweenOrderByMonthValueAsc(patient, startDate, endDate))
                .thenReturn(existingRows)
                .thenReturn(existingRows);

        when(patientRepository.save(any(Patient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<InsuranceMonth> result = insuranceService.getLastSixMonthsForPatient(patient);

        assertNotNull(result);
        assertEquals(6, result.size());

        verify(insuranceMonthRepository, never()).save(any(InsuranceMonth.class));
        verify(patientRepository).save(patientCaptor.capture());

        Patient savedPatient = patientCaptor.getValue();
        assertFalse(savedPatient.isInsurancePaidLast6Months());
    }

    @Test
    void getLastSixMonthsForPatient_shouldSetInsurancePaidLast6MonthsTrue_whenAllSixMonthsArePaid() {
        Patient patient = buildPatient(3L);

        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(5);
        LocalDate startDate = startMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        List<InsuranceMonth> existingPaidRows = buildInsuranceRowsForRange(patient, startMonth, 6, true);

        when(insuranceMonthRepository.findByPatientAndMonthValueBetweenOrderByMonthValueAsc(patient, startDate, endDate))
                .thenReturn(existingPaidRows)
                .thenReturn(existingPaidRows);

        when(patientRepository.save(any(Patient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<InsuranceMonth> result = insuranceService.getLastSixMonthsForPatient(patient);

        assertNotNull(result);
        assertEquals(6, result.size());

        verify(patientRepository).save(patientCaptor.capture());
        Patient savedPatient = patientCaptor.getValue();
        assertTrue(savedPatient.isInsurancePaidLast6Months());
    }

    @Test
    void payMonth_shouldCreateRowAndMarkPaid_whenRowDoesNotExist() {
        Patient patient = buildPatient(4L);

        YearMonth monthToPay = YearMonth.now().minusMonths(1);
        LocalDate monthValue = monthToPay.atDay(1);

        when(insuranceMonthRepository.findByPatientAndMonthValue(patient, monthValue))
                .thenReturn(Optional.empty());

        when(insuranceMonthRepository.save(any(InsuranceMonth.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(5);
        LocalDate startDate = startMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        List<InsuranceMonth> refreshedRows = buildInsuranceRowsForRange(patient, startMonth, 6, false);

        when(insuranceMonthRepository.findByPatientAndMonthValueBetweenOrderByMonthValueAsc(patient, startDate, endDate))
                .thenReturn(refreshedRows)
                .thenReturn(refreshedRows);

        when(patientRepository.save(any(Patient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        insuranceService.payMonth(patient, monthToPay);

        verify(insuranceMonthRepository, atLeastOnce()).save(insuranceMonthCaptor.capture());

        List<InsuranceMonth> savedInsuranceMonths = insuranceMonthCaptor.getAllValues();
        InsuranceMonth lastSavedInsuranceMonth = savedInsuranceMonths.get(savedInsuranceMonths.size() - 1);

        assertTrue(lastSavedInsuranceMonth.isPaid());
        assertNotNull(lastSavedInsuranceMonth.getPaidAt());
        assertEquals(patient, lastSavedInsuranceMonth.getPatient());
        assertEquals(monthToPay, lastSavedInsuranceMonth.getMonth());
    }

    @Test
    void payMonth_shouldMarkPaidAndSetPaidAt_whenExistingRowIsUnpaid() {
        Patient patient = buildPatient(5L);

        YearMonth monthToPay = YearMonth.now().minusMonths(2);
        LocalDate monthValue = monthToPay.atDay(1);

        InsuranceMonth existingInsuranceMonth = new InsuranceMonth();
        existingInsuranceMonth.setPatient(patient);
        existingInsuranceMonth.setMonth(monthToPay);
        existingInsuranceMonth.setPaid(false);

        when(insuranceMonthRepository.findByPatientAndMonthValue(patient, monthValue))
                .thenReturn(Optional.of(existingInsuranceMonth));

        when(insuranceMonthRepository.save(any(InsuranceMonth.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(5);
        LocalDate startDate = startMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        List<InsuranceMonth> refreshedRows = buildInsuranceRowsForRange(patient, startMonth, 6, false);

        when(insuranceMonthRepository.findByPatientAndMonthValueBetweenOrderByMonthValueAsc(patient, startDate, endDate))
                .thenReturn(refreshedRows)
                .thenReturn(refreshedRows);

        when(patientRepository.save(any(Patient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        insuranceService.payMonth(patient, monthToPay);

        verify(insuranceMonthRepository).save(insuranceMonthCaptor.capture());
        InsuranceMonth savedInsuranceMonth = insuranceMonthCaptor.getValue();

        assertTrue(savedInsuranceMonth.isPaid());
        assertNotNull(savedInsuranceMonth.getPaidAt());
        assertEquals(monthToPay, savedInsuranceMonth.getMonth());
    }

    @Test
    void payMonth_shouldNotSaveAgain_whenExistingRowIsAlreadyPaid() {
        Patient patient = buildPatient(6L);

        YearMonth monthToPay = YearMonth.now().minusMonths(3);
        LocalDate monthValue = monthToPay.atDay(1);

        InsuranceMonth existingPaidInsuranceMonth = new InsuranceMonth();
        existingPaidInsuranceMonth.setPatient(patient);
        existingPaidInsuranceMonth.setMonth(monthToPay);
        existingPaidInsuranceMonth.setPaid(true);
        existingPaidInsuranceMonth.setPaidAt(LocalDate.now().minusDays(2));

        when(insuranceMonthRepository.findByPatientAndMonthValue(patient, monthValue))
                .thenReturn(Optional.of(existingPaidInsuranceMonth));

        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(5);
        LocalDate startDate = startMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        List<InsuranceMonth> refreshedRows = buildInsuranceRowsForRange(patient, startMonth, 6, false);

        when(insuranceMonthRepository.findByPatientAndMonthValueBetweenOrderByMonthValueAsc(patient, startDate, endDate))
                .thenReturn(refreshedRows)
                .thenReturn(refreshedRows);

        when(patientRepository.save(any(Patient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        insuranceService.payMonth(patient, monthToPay);

        verify(insuranceMonthRepository, never()).save(existingPaidInsuranceMonth);
        verify(insuranceMonthRepository, never()).save(insuranceMonthCaptor.capture());
    }

    private Patient buildPatient(Long patientId) {
        Patient patient = new Patient();
        patient.setId(patientId);
        patient.setFirstName("PatientFirstName");
        patient.setLastName("PatientLastName");
        patient.setEgn("1234567890");
        patient.setInsurancePaidLast6Months(false);
        return patient;
    }

    private List<InsuranceMonth> buildInsuranceRowsForRange(Patient patient,
                                                            YearMonth startMonth,
                                                            int monthsToBuild,
                                                            boolean paid) {
        List<InsuranceMonth> insuranceMonths = new ArrayList<>();
        for (int monthOffset = 0; monthOffset < monthsToBuild; monthOffset++) {
            YearMonth month = startMonth.plusMonths(monthOffset);
            InsuranceMonth insuranceMonth = new InsuranceMonth();
            insuranceMonth.setPatient(patient);
            insuranceMonth.setMonth(month);
            insuranceMonth.setPaid(paid);
            if (paid) {
                insuranceMonth.setPaidAt(LocalDate.now().minusDays(1));
            }
            insuranceMonths.add(insuranceMonth);
        }
        return insuranceMonths;
    }
}
