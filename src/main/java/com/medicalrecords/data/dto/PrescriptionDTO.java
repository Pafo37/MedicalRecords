package com.medicalrecords.data.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PrescriptionDTO {

    @Size(max = 2000, message = "Maximum character length is 2000")
    private String instructions;
}