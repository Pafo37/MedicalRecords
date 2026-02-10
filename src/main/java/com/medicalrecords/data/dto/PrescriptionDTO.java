package com.medicalrecords.data.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PrescriptionDTO {

    private String instructions;
}