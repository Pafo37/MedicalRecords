package com.medicalrecords.data.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePatientDTO {

    @Size(min = 3, message = "Minimum characters must be 3")
    @NotNull(message = "First name must not be null")
    private String firstName;

    @Size(min = 3, message = "Minimum characters must be 3")
    @NotNull(message = "Last name must not be null")
    private String lastName;

    private String egn;
    private Long primaryCareDoctorId;
}
