package com.medicalrecords.data.dto;

import lombok.Data;

@Data
public class CreatePrescriptionItemDTO {

    private String medicationName;
    private String instructions;
}