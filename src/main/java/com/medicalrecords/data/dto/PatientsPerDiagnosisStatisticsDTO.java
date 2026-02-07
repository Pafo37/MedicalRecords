package com.medicalrecords.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientsPerDiagnosisStatisticsDTO {

    private Long diagnosisId;
    private String diagnosisName;
    private Long patientsCount;

}
