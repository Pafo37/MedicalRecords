package com.medicalrecords.data.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisPatientCountDTO {

    @Size(max = 2000, message = "Maximum character length is 2000")
    private String diagnosisName;
    private Long distinctPatientCount;
}