package com.medicalrecords.service.appointment;

import com.medicalrecords.data.dto.CreateAppointmentRequest;
import com.medicalrecords.data.dto.CreatePrescriptionItemRequest;
import com.medicalrecords.data.dto.CreateSickLeaveRequest;
import com.medicalrecords.data.entity.*;
import com.medicalrecords.data.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final SickLeaveRepository sickLeaveRepository;

    @Override
    @Transactional
    public Long create(CreateAppointmentRequest request) {

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        Diagnosis diagnosis = diagnosisRepository.findById(request.getDiagnosisId())
                .orElseThrow(() -> new IllegalArgumentException("Diagnosis not found"));

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setDiagnosis(diagnosis);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        List<CreatePrescriptionItemRequest> prescriptions =
                request.getPrescriptions() == null
                        ? Collections.emptyList()
                        : request.getPrescriptions();

        for (CreatePrescriptionItemRequest createPrescriptionItemRequest : prescriptions) {
            PrescriptionItem item = new PrescriptionItem();
            item.setAppointment(savedAppointment);
            item.setMedicationName(createPrescriptionItemRequest.getMedicationName());
            item.setInstructions(createPrescriptionItemRequest.getInstructions());
            prescriptionItemRepository.save(item);
        }

        CreateSickLeaveRequest createSickLeaveRequest = request.getSickLeave();
        if (createSickLeaveRequest != null) {
            LocalDate startDate = createSickLeaveRequest.getStartDate();
            int days = createSickLeaveRequest.getDays();
            LocalDate endDate = startDate.plusDays(days - 1);

            SickLeave sickLeave = new SickLeave();
            sickLeave.setAppointment(savedAppointment);
            sickLeave.setPatient(patient);
            sickLeave.setDoctor(doctor);
            sickLeave.setStartDate(startDate);
            sickLeave.setDays(days);
            sickLeave.setEndDate(endDate);

            sickLeaveRepository.save(sickLeave);
        }

        return savedAppointment.getId();
    }

    @Override
    @Transactional()
    public List<Appointment> getPatientHistory(Long patientId) {
        return appointmentRepository
                .findAllByPatientIdOrderByAppointmentDateDesc(patientId);
    }

    @Override
    @Transactional()
    public List<Appointment> getDoctorHistory(Long doctorId) {
        return appointmentRepository
                .findAllByDoctorIdOrderByAppointmentDateDesc(doctorId);
    }
}