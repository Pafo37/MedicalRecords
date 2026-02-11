package com.medicalrecords.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisPatientCountDTO {
    private String diagnosisName;
    private Long distinctPatientCount;
}