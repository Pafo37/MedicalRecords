package com.medicalrecords.data.dto;


import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateAppointmentDTO {

    private Long patientId;
    private Long doctorId;
    private LocalDate appointmentDate;
    private Long diagnosisId;

    // optional
    private List<PrescriptionDTO> prescriptions;
    private CreateSickLeaveDTO sickLeave;
}
