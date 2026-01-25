package com.medicalrecords.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "primary_care_doctor_id", nullable = false)
    private Doctor primaryCareDoctor;

    @OneToMany(mappedBy = "patient", orphanRemoval = true)
    private List<HealthInsurance> healthInsurances = new ArrayList<>();

}