package com.medicalrecords.service.doctor;


import com.medicalrecords.data.dto.CreateDoctorDTO;
import com.medicalrecords.data.entity.Doctor;

import java.util.List;

public interface DoctorService {

    Long create(CreateDoctorDTO dto);

    Doctor getById(Long doctorId);

    List<Doctor> getAll();
}
