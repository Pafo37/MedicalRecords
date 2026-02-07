package com.medicalrecords.service.patient;

import com.medicalrecords.data.dto.CreatePatientDTO;
import com.medicalrecords.data.entity.Patient;

import java.util.List;

public interface PatientService {

    Long create(CreatePatientDTO dto);

    Patient getById(Long patientId);

    List<Patient> getAll();
}
