package com.medicalrecords.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDTO {

    @NotNull(message = "Doctor id must not be null")
    private Long doctorId;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime visitDate;

    @Size(max = 2000, message = "Maximum character length is 2000")
    @NotBlank(message = "Notes should not be blank")
    private String notes;
}