package com.medicalrecords.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;

@Entity
public class Treatment extends BaseEntity{
    //TODO: might reduce those
    private String medicationName;
    private String dosage;
    private String instructions;


}