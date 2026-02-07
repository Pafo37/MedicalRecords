package com.medicalrecords.data.dto;

import lombok.Data;

@Data
public class CreatePatientDTO {
    private String firstName;
    private String lastName;
    private String egn;
    private Long primaryCareDoctorId;
}
