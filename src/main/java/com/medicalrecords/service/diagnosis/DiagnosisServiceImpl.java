package com.medicalrecords.service.diagnosis;

import com.medicalrecords.data.dto.CreateDiagnosisDTO;
import com.medicalrecords.data.entity.Diagnosis;
import com.medicalrecords.data.repository.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiagnosisServiceImpl implements DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;

    @Override
    public Long create(CreateDiagnosisDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("name is required");
        }

        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setName(dto.getName());

        Diagnosis saved = diagnosisRepository.save(diagnosis);
        return saved.getId();
    }

    @Override
    public Diagnosis getById(Long diagnosisId) {
        return diagnosisRepository.findById(diagnosisId)
                .orElseThrow(() -> new IllegalArgumentException("Diagnosis not found"));
    }

    @Override
    public List<Diagnosis> getAll() {
        return diagnosisRepository.findAll();
    }
}