package com.medicalrecords.api;

import com.medicalrecords.data.dto.CreatePatientDTO;
import com.medicalrecords.data.entity.Patient;
import com.medicalrecords.service.patient.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientRestController {

    private final PatientService patientService;

    @PostMapping
    public Long create(@RequestBody CreatePatientDTO dto) {
        return patientService.create(dto);
    }

    @GetMapping("/{patientId}")
    public Patient getById(@PathVariable Long patientId) {
        return patientService.getById(patientId);
    }

    @GetMapping
    public List<Patient> getAll() {
        return patientService.getAll();
    }
}