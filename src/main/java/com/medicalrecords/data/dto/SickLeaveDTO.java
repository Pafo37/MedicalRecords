package com.medicalrecords.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SickLeaveDTO {

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

}
