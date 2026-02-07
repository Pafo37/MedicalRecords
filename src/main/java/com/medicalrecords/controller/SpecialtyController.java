package com.medicalrecords.controller;

import com.medicalrecords.data.dto.AssignSpecialtiesToDoctorDTO;
import com.medicalrecords.data.dto.CreateSpecialtyDTO;
import com.medicalrecords.data.entity.Specialty;
import com.medicalrecords.service.specialty.SpecialtyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specialties")
@RequiredArgsConstructor
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @PostMapping
    public Long create(@RequestBody CreateSpecialtyDTO dto) {
        return specialtyService.create(dto);
    }

    @GetMapping("/{specialtyId}")
    public Specialty getById(@PathVariable Long specialtyId) {
        return specialtyService.getById(specialtyId);
    }

    @GetMapping
    public List<Specialty> getAll() {
        return specialtyService.getAll();
    }

    @PostMapping("/assign-to-doctor/{doctorId}")
    public void assignToDoctor(@PathVariable Long doctorId,
                               @RequestBody AssignSpecialtiesToDoctorDTO dto) {
        specialtyService.assignToDoctor(doctorId, dto);
    }
}