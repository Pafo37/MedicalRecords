package com.medicalrecords.api;

import com.medicalrecords.data.dto.CreateDiagnosisDTO;
import com.medicalrecords.data.entity.Diagnosis;
import com.medicalrecords.service.diagnosis.DiagnosisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnoses")
@RequiredArgsConstructor
public class DiagnosisRestController {

    private final DiagnosisService diagnosisService;

    @PostMapping
    public Long create(@RequestBody CreateDiagnosisDTO dto) {
        return diagnosisService.create(dto);
    }

    @GetMapping("/{diagnosisId}")
    public Diagnosis getById(@PathVariable Long diagnosisId) {
        return diagnosisService.getById(diagnosisId);
    }

    @GetMapping
    public List<Diagnosis> getAll() {
        return diagnosisService.getAll();
    }
}
