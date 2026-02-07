package com.medicalrecords.service.diagnosis;

import com.medicalrecords.data.dto.CreateDiagnosisDTO;
import com.medicalrecords.data.entity.Diagnosis;

import java.util.List;

public interface DiagnosisService {

    Long create(CreateDiagnosisDTO dto);

    Diagnosis getById(Long diagnosisId);

    List<Diagnosis> getAll();
}