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
@Table(name = "patients")
public class Patient extends BaseEntity {

    @Size(min = 3, message = "Minimum character must be 3")
    @NotNull(message = "First name must not be null")
    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Size(min = 3, message = "Minimum character must be 3")
    @NotNull(message = "Last name must not be null")
    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @Size(min = 10, max = 10, message = "EGN must be exactly 10 characters")
    @Column(name = "egn", nullable = false, unique = true, length = 10)
    private String egn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_care_doctor_id")
    private Doctor primaryCareDoctor;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_insurance_paid_last_six_months")
    private boolean isInsurancePaidLast6Months = false;

}