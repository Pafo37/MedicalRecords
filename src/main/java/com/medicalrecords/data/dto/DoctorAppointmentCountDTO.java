package com.medicalrecords.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAppointmentCountDTO {
    private Long doctorId;
    private String doctorMedicalId;
    private String doctorFirstName;
    private String doctorLastName;
    private Long totalAppointmentCount;
    private Long completedAppointmentCount;
}
