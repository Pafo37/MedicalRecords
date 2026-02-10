package com.medicalrecords.data.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Table(
        name = "insurance_months",
        uniqueConstraints = @UniqueConstraint(columnNames = {"patient_id", "month_value"})
)
@Getter
@Setter
public class InsuranceMonth extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // store month as first day of that month to keep it simple in MySQL
    @Column(name = "month_value", nullable = false)
    private LocalDate monthValue; // e.g. 2026-02-01

    @Column(name = "paid", nullable = false)
    private boolean paid;

    @Column(name = "paid_at")
    private LocalDate paidAt;

    public YearMonth getMonth() {
        return YearMonth.from(monthValue);
    }

    public void setMonth(YearMonth month) {
        this.monthValue = month.atDay(1);
    }
}