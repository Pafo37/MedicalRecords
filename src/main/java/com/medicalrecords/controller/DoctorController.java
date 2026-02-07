package com.medicalrecords.controller;


import com.medicalrecords.data.dto.CreateDoctorDTO;
import com.medicalrecords.data.entity.Doctor;
import com.medicalrecords.service.doctor.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    public Long create(@RequestBody CreateDoctorDTO dto) {
        return doctorService.create(dto);
    }

    @GetMapping("/{doctorId}")
    public Doctor getById(@PathVariable Long doctorId) {
        return doctorService.getById(doctorId);
    }

    @GetMapping
    public List<Doctor> getAll() {
        return doctorService.getAll();
    }
}