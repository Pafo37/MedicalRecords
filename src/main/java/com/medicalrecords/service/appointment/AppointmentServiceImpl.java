package com.medicalrecords.service.appointment;

import com.medicalrecords.data.dto.AppointmentDTO;
import com.medicalrecords.data.dto.CreateAppointmentDTO;
import com.medicalrecords.data.dto.CreatePrescriptionItemDTO;
import com.medicalrecords.data.dto.CreateSickLeaveDTO;
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

    @Override
    @Transactional
    public Appointment createAppointment(AppointmentDTO dto, String patientKeycloakId) {

        Patient patient = patientRepository.findByUser_KeycloakId(patientKeycloakId)
                .orElseThrow(() -> new IllegalStateException("Current patient not found."));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found."));

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .visitDate(dto.getVisitDate())
                .notes(dto.getNotes())
                .build();

        return appointmentRepository.save(appointment);
    }
}