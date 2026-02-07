package com.medicalrecords.controller;

import com.medicalrecords.data.dto.CreateAppointmentDTO;
import com.medicalrecords.data.entity.Appointment;
import com.medicalrecords.service.appointment.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public Long create(@RequestBody CreateAppointmentDTO request) {
        return appointmentService.create(request);
    }

    @GetMapping("/patient/{patientId}")
    public List<Appointment> getPatientHistory(@PathVariable Long patientId) {
        return appointmentService.getPatientHistory(patientId);
    }

    @GetMapping("/doctor/{doctorId}")
    public List<Appointment> getDoctorHistory(@PathVariable Long doctorId) {
        return appointmentService.getDoctorHistory(doctorId);
    }
}
