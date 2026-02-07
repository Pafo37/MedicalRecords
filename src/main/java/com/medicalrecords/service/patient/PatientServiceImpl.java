package com.medicalrecords.service.patient;

import com.medicalrecords.data.dto.CreatePatientDTO;
import com.medicalrecords.data.entity.Doctor;
import com.medicalrecords.data.entity.Patient;
import com.medicalrecords.data.repository.DoctorRepository;
import com.medicalrecords.data.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Override
    public Long create(CreatePatientDTO dto) {

        if (dto.getFirstName() == null || dto.getFirstName().isBlank()) {
            throw new IllegalArgumentException("firstName is required");
        }
        if (dto.getLastName() == null || dto.getLastName().isBlank()) {
            throw new IllegalArgumentException("lastName is required");
        }
        if (dto.getEgn() == null || dto.getEgn().isBlank()) {
            throw new IllegalArgumentException("egn is required");
        }
        if (dto.getPrimaryCareDoctorId() == null) {
            throw new IllegalArgumentException("primaryCareDoctorId is required");
        }

        patientRepository.findByEgn(dto.getEgn())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Patient with this EGN already exists");
                });

        Doctor primaryCareDoctor = doctorRepository.findById(dto.getPrimaryCareDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Primary care doctor not found"));

        Patient patient = new Patient();
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setEgn(dto.getEgn());
        patient.setPrimaryCareDoctor(primaryCareDoctor);

        Patient saved = patientRepository.save(patient);
        return saved.getId();
    }

    @Override
    public Patient getById(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
    }

    @Override
    public List<Patient> getAll() {
        return patientRepository.findAll();
    }
}