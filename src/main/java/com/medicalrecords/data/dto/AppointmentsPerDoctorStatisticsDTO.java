package com.medicalrecords.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentsPerDoctorStatisticsDTO {

    private Long doctorId;
    private String medicalId;
    private String firstName;
    private String lastName;
    private Long appointmentsCount;
}
