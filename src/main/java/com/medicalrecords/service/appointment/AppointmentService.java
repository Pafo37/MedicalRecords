package com.medicalrecords.service.appointment;

import com.medicalrecords.data.dto.AppointmentCompleteDTO;
import com.medicalrecords.data.dto.AppointmentDTO;
import com.medicalrecords.data.dto.PrescriptionDTO;
import com.medicalrecords.data.dto.SickLeaveDTO;
import com.medicalrecords.data.entity.Appointment;

import java.util.List;

public interface AppointmentService {

    Appointment createAppointment(AppointmentDTO dto, String patientKeycloakId);

    List<Appointment> getAppointmentsForDoctor(String doctorKeycloakId);

    Appointment getAppointmentForDoctor(Long appointmentId, String doctorKeycloakId);

    void updateDoctorNotes(Long appointmentId, String doctorKeycloakId, String doctorNotes);

    void addPrescription(Long appointmentId, String doctorKeycloakId, PrescriptionDTO prescriptionData);

    void upsertSickLeave(Long appointmentId, String doctorKeycloakId, SickLeaveDTO sickLeaveData);

    void completeAppointment(Long appointmentId, String doctorKeycloakId, AppointmentCompleteDTO formData);

}