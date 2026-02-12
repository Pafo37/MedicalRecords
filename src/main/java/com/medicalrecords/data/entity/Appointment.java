package com.medicalrecords.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment extends BaseEntity {

    @Column(nullable = false)
    private LocalDateTime visitDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Size(max = 2000, message = "Maximum character length is 2000")
    @Column(length = 2000)
    private String notes;

    @Size(max = 4000, message = "Maximum character length is 4000")
    @Column(name = "doctor_notes", length = 4000)
    private String doctorNotes;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    private SickLeave sickLeave;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnosis_id")
    private Diagnosis diagnosis;

    @Size(max = 4000, message = "Maximum character length is 4000")
    @Column(name = "prescription_instructions", length = 4000)
    private String prescriptionInstructions;

    @Column(name = "completed", nullable = false)
    @Builder.Default
    private boolean completed = false;


}