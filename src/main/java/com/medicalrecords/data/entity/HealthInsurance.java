package com.medicalrecords.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "health_insurance")
@Getter
@Setter
@NoArgsConstructor
public class HealthInsurance extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull(message = "Month must not be null")
    @Column(name = "month", nullable = false)
    private LocalDate month;

    @Column(name = "is_paid", nullable = false)
    private boolean paid;
}
