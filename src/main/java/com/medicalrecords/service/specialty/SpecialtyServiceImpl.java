package com.medicalrecords.service.specialty;

import com.medicalrecords.data.dto.AssignSpecialtiesToDoctorDTO;
import com.medicalrecords.data.dto.CreateSpecialtyDTO;
import com.medicalrecords.data.entity.Doctor;
import com.medicalrecords.data.entity.Specialty;
import com.medicalrecords.data.repository.DoctorRepository;
import com.medicalrecords.data.repository.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final DoctorRepository doctorRepository;

    @Override
    public Long create(CreateSpecialtyDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("name is required");
        }

        Specialty specialty = new Specialty();
        specialty.setName(dto.getName());

        Specialty saved = specialtyRepository.save(specialty);
        return saved.getId();
    }

    @Override
    public Specialty getById(Long specialtyId) {
        return specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new IllegalArgumentException("Specialty not found"));
    }

    @Override
    public List<Specialty> getAll() {
        return specialtyRepository.findAll();
    }

    @Override
    public void assignToDoctor(Long doctorId, AssignSpecialtiesToDoctorDTO dto) {
        if (dto.getSpecialtyIds() == null || dto.getSpecialtyIds().isEmpty()) {
            throw new IllegalArgumentException("specialtyIds is required");
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        List<Specialty> specialties = specialtyRepository.findAllById(dto.getSpecialtyIds());

        if (specialties.size() != dto.getSpecialtyIds().size()) {
            throw new IllegalArgumentException("One or more specialties not found");
        }

        Set<Specialty> updated = new HashSet<>(doctor.getSpecialties());
        updated.addAll(specialties);
        doctor.setSpecialties(updated);

        doctorRepository.save(doctor);
    }
}
