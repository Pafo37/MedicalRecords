package com.medicalrecords.data.dto;

import lombok.Data;

@Data
public class CreateDoctorDTO {
    private String medicalId;
    private String firstName;
    private String lastName;
}