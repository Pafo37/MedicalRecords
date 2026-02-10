package com.medicalrecords.service.appointment;

import com.medicalrecords.data.dto.AppointmentDTO;
import com.medicalrecords.data.entity.Appointment;

import java.util.List;

public interface AppointmentService {

    Appointment createAppointment(AppointmentDTO dto, String patientKeycloakId);
}