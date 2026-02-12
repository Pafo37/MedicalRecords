package com.medicalrecords.data.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentCompleteDTO {

    private String doctorNotes;

    private String prescriptionInstructions;

    private LocalDate sickLeaveStartDate;
    private LocalDate sickLeaveEndDate;

    @NotBlank(message = "Diagnosis must not be blank")
    private String diagnosis;

}