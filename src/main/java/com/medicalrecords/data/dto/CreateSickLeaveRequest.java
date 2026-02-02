package com.medicalrecords.data.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateSickLeaveRequest {

    private LocalDate startDate;
    private Integer days;
}