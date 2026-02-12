package com.medicalrecords.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDoctorNotesDTO {

    @Size(max = 2000, message = "Maximum character length is 2000")
    private String doctorNotes;
}
