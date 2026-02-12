package com.medicalrecords.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentCompleteDTO {

    @Size(max = 2000, message = "Maximum character length is 2000")
    private String doctorNotes;

    @Size(max = 2000, message = "Maximum character length is 2000")
    private String prescriptionInstructions;

    private LocalDate sickLeaveStartDate;
    private LocalDate sickLeaveEndDate;

    @NotBlank(message = "Diagnosis must not be blank")
    private String diagnosis;

}