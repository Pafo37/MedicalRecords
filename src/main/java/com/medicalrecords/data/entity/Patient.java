package com.medicalrecords.data.entity;

import jakarta.persistence.*;
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

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

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