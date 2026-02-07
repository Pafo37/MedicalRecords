package com.medicalrecords.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentHistoryItemDTO {

    private Long appointmentId;
    private LocalDate appointmentDate;

    private Long patientId;
    private String patientFirstName;
    private String patientLastName;
    private String patientEgn;

    private Long doctorId;
    private String doctorMedicalId;
    private String doctorFirstName;
    private String doctorLastName;

    private Long diagnosisId;
    private String diagnosisName;

}