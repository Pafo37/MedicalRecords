package com.medicalrecords.service.specialty;

import com.medicalrecords.data.dto.AssignSpecialtiesToDoctorDTO;
import com.medicalrecords.data.dto.CreateSpecialtyDTO;
import com.medicalrecords.data.entity.Specialty;

import java.util.List;

public interface SpecialtyService {

    Long create(CreateSpecialtyDTO dto);

    Specialty getById(Long specialtyId);

    List<Specialty> getAll();

    void assignToDoctor(Long doctorId, AssignSpecialtiesToDoctorDTO dto);
}