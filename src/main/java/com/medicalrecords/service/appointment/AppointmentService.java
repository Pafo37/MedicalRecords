package com.medicalrecords.service.appointment;

import com.medicalrecords.data.dto.CreateAppointmentRequest;
import com.medicalrecords.data.entity.Appointment;

import java.util.List;

public interface AppointmentService {

    Long create(CreateAppointmentRequest request);

    List<Appointment> getPatientHistory(Long patientId);

    List<Appointment> getDoctorHistory(Long doctorId);
}