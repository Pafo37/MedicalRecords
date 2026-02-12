package com.medicalrecords.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "doctors")
public class Doctor extends BaseEntity {

    @NotNull(message = "Medical id must not be null")
    @Column(name = "medical_id", nullable = false, unique = true, length = 64)
    private String medicalId;

    @Size(min = 3, message = "Minimum characters must be 3")
    @NotNull(message = "First name must not be null")
    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Size(min = 3, message = "Minimum characters must be 3")
    @NotNull(message = "Last name must not be null")
    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull(message = "Specialty must not be null")
    @Column(nullable = false)
    private String specialty;

    @NotNull(message = "Personal doctor must not be null")
    @Column(nullable = false)
    private boolean isPersonalDoctor = false;

}