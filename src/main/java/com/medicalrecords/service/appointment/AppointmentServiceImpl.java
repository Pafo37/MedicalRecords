package com.medicalrecords.service.appointment;

import com.medicalrecords.data.dto.AppointmentCompleteDTO;
import com.medicalrecords.data.dto.AppointmentDTO;
import com.medicalrecords.data.dto.PrescriptionDTO;
import com.medicalrecords.data.dto.SickLeaveDTO;
import com.medicalrecords.data.entity.*;
import com.medicalrecords.data.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final SickLeaveRepository sickLeaveRepository;


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

    @Override
    public List<Appointment> getAppointmentsForDoctor(String doctorKeycloakId) {
        return appointmentRepository.findAllByDoctor_User_KeycloakIdOrderByVisitDateAsc(doctorKeycloakId);
    }

    @Override
    public Appointment getAppointmentForDoctor(Long appointmentId, String doctorKeycloakId) {
        return appointmentRepository.findByIdAndDoctor_User_KeycloakId(appointmentId, doctorKeycloakId)
                .orElseThrow(() -> new SecurityException("Appointment not found or you are not assigned to it."));
    }

    private void assertAppointmentIsToday(Appointment appointment) {
        LocalDate todayInBusinessZone = LocalDate.now();
        LocalDate appointmentDate = appointment.getVisitDate().toLocalDate();

        if (!appointmentDate.equals(todayInBusinessZone)) {
            throw new IllegalStateException("You can complete an appointment only on its scheduled day.");
        }
    }

    @Override
    @Transactional
    public void updateDoctorNotes(Long appointmentId, String doctorKeycloakId, String doctorNotes) {
        Appointment appointment = getAppointmentForDoctor(appointmentId, doctorKeycloakId);
        assertAppointmentIsToday(appointment);

        appointment.setDoctorNotes(doctorNotes);
        appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public void addPrescription(Long appointmentId, String doctorKeycloakId, PrescriptionDTO prescriptionData) {
        Appointment appointment = getAppointmentForDoctor(appointmentId, doctorKeycloakId);
        assertAppointmentIsToday(appointment);

        Prescription prescription = Prescription.builder()
                .appointment(appointment)
                .instructions(prescriptionData.getInstructions())
                .build();

        prescriptionRepository.save(prescription);
    }

    @Override
    @Transactional
    public void upsertSickLeave(Long appointmentId, String doctorKeycloakId, SickLeaveDTO sickLeaveData) {
        Appointment appointment = getAppointmentForDoctor(appointmentId, doctorKeycloakId);
        assertAppointmentIsToday(appointment);

        if (sickLeaveData.getEndDate().isBefore(sickLeaveData.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }

        SickLeave sickLeave = sickLeaveRepository.findByAppointment_Id(appointmentId)
                .orElseGet(() -> SickLeave.builder().appointment(appointment).build());

        sickLeave.setStartDate(sickLeaveData.getStartDate());
        sickLeave.setEndDate(sickLeaveData.getEndDate());

        sickLeaveRepository.save(sickLeave);
    }

    @Override
    @Transactional
    public void completeAppointment(Long appointmentId, String doctorKeycloakId, AppointmentCompleteDTO formData) {

        Appointment appointment = getAppointmentForDoctor(appointmentId, doctorKeycloakId);
        assertAppointmentIsToday(appointment);

        // 1) Save doctor notes
        appointment.setDoctorNotes(formData.getDoctorNotes());

        // 2) Create prescription if instructions provided
        String prescriptionInstructions = formData.getPrescriptionInstructions();
        if (prescriptionInstructions != null && !prescriptionInstructions.trim().isEmpty()) {
            Prescription prescription = Prescription.builder()
                    .appointment(appointment)
                    .instructions(prescriptionInstructions.trim())
                    .build();
            appointment.setPrescriptionInstructions(formData.getPrescriptionInstructions());
            prescriptionRepository.save(prescription);
        }

        LocalDate sickLeaveStartDate = formData.getSickLeaveStartDate();
        LocalDate sickLeaveEndDate = formData.getSickLeaveEndDate();

        boolean hasAnySickLeaveField = sickLeaveStartDate != null || sickLeaveEndDate != null;

        if (hasAnySickLeaveField) {
            if (sickLeaveStartDate == null || sickLeaveEndDate == null) {
                throw new IllegalArgumentException("Sick leave requires both start and end date.");
            }

            if (sickLeaveEndDate.isBefore(sickLeaveStartDate)) {
                throw new IllegalArgumentException("End date cannot be before start date.");
            }

            SickLeave sickLeave = sickLeaveRepository.findByAppointment_Id(appointmentId)
                    .orElseGet(() -> SickLeave.builder().appointment(appointment).build());

            sickLeave.setStartDate(sickLeaveStartDate);
            sickLeave.setEndDate(sickLeaveEndDate);

            sickLeaveRepository.save(sickLeave);
        }

        appointment.setCompleted(true);
        appointmentRepository.save(appointment);

    }
}
