package com.medicalrecords.data.dto;

import lombok.Data;

@Data
public class CreatePrescriptionItemRequest {

    private String medicationName;
    private String instructions;
}