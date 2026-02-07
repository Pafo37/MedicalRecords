package com.medicalrecords.data.dto;

import lombok.Data;

import java.util.List;

@Data
public class AssignSpecialtiesToDoctorDTO {
    private List<Long> specialtyIds;
}