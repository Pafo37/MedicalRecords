package com.medicalrecords.service.doctor;


import com.medicalrecords.data.dto.CreateDoctorDTO;
import com.medicalrecords.data.dto.RegistrationDTO;
import com.medicalrecords.data.entity.Doctor;
import com.medicalrecords.data.entity.User;

import java.util.List;

public interface DoctorService {

    Long create(CreateDoctorDTO dto);

    Doctor getById(Long doctorId);

    List<Doctor> getAll();

    void createFromRegistration(RegistrationDTO dto, User user);
}
