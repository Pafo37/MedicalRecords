package com.medicalrecords.service.doctor;

import com.medicalrecords.data.dto.CreateDoctorDTO;
import com.medicalrecords.data.entity.Doctor;
import com.medicalrecords.data.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

    @Override
    public Long create(CreateDoctorDTO dto) {

        if (dto.getMedicalId() == null || dto.getMedicalId().isBlank()) {
            throw new IllegalArgumentException("medicalId is required");
        }
        if (dto.getFirstName() == null || dto.getFirstName().isBlank()) {
            throw new IllegalArgumentException("firstName is required");
        }
        if (dto.getLastName() == null || dto.getLastName().isBlank()) {
            throw new IllegalArgumentException("lastName is required");
        }

        doctorRepository.findByMedicalId(dto.getMedicalId())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Doctor with this medicalId already exists");
                });

        Doctor doctor = new Doctor();
        doctor.setMedicalId(dto.getMedicalId());
        doctor.setFirstName(dto.getFirstName());
        doctor.setLastName(dto.getLastName());

        Doctor saved = doctorRepository.save(doctor);
        return saved.getId();
    }

    @Override
    public Doctor getById(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
    }

    @Override
    public List<Doctor> getAll() {
        return doctorRepository.findAll();
    }
}