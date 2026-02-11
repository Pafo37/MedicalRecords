package com.medicalrecords.data.entity;

import jakarta.persistence.*;
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

    @Column(length = 2000)
    private String notes;

    @Column(name = "doctor_notes", length = 4000)
    private String doctorNotes;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    private SickLeave sickLeave;

    @Column(name = "prescription_instructions", length = 4000)
    private String prescriptionInstructions;

    @Column(name = "completed", nullable = false)
    @Builder.Default
    private boolean completed = false;


}