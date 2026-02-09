package com.medicalrecords.service.patient;

import com.medicalrecords.data.dto.CreatePatientDTO;
import com.medicalrecords.data.dto.RegistrationDTO;
import com.medicalrecords.data.entity.Patient;
import com.medicalrecords.data.entity.User;

import java.util.List;

public interface PatientService {

    Long create(CreatePatientDTO dto);

    Patient getById(Long patientId);

    List<Patient> getAll();

    void createFromRegistration(RegistrationDTO dto, User user);
}
