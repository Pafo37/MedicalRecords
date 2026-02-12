package com.medicalrecords.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorPrimaryPatientCountDTO {

    @NotNull(message = "Id must not be null")
    private Long doctorId;

    @NotNull(message = "Id must not be null")
    private String doctorMedicalId;

    @NotBlank(message = "First name must not be blank")
    private String doctorFirstName;

    @NotBlank(message = "Last name must not be blank")
    private String doctorLastName;
    private Long primaryPatientCount;
}
