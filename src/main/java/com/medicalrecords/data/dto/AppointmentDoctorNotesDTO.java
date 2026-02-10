package com.medicalrecords.data.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDoctorNotesDTO {

    @NotBlank
    private String doctorNotes;
}
