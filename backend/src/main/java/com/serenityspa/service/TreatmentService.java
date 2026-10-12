package com.serenityspa.service;

import com.serenityspa.dto.TreatmentRequest;
import com.serenityspa.dto.TreatmentResponse;
import com.serenityspa.entity.Treatment;
import com.serenityspa.exception.ResourceNotFoundException;
import com.serenityspa.repository.TreatmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TreatmentService {

    private final TreatmentRepository treatmentRepository;

    public TreatmentService(TreatmentRepository treatmentRepository) {
        this.treatmentRepository = treatmentRepository;
    }

    public List<TreatmentResponse> getAllActiveTreatments() {
        return treatmentRepository.findByActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TreatmentResponse> getAllTreatmentsAdmin() {
        return treatmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TreatmentResponse getTreatmentById(Long id) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found with id: " + id));
        return mapToResponse(treatment);
    }

    @Transactional
    public TreatmentResponse createTreatment(TreatmentRequest request) {
        Treatment treatment = new Treatment(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getDurationMinutes(),
                request.getImagePath(),
                request.getActive() != null ? request.getActive() : true
        );

        Treatment saved = treatmentRepository.save(treatment);
        return mapToResponse(saved);
    }

    @Transactional
    public TreatmentResponse updateTreatment(Long id, TreatmentRequest request) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found with id: " + id));

        treatment.setName(request.getName());
        treatment.setDescription(request.getDescription());
        treatment.setPrice(request.getPrice());
        treatment.setDurationMinutes(request.getDurationMinutes());
        if (request.getImagePath() != null) {
            treatment.setImagePath(request.getImagePath());
        }
        if (request.getActive() != null) {
            treatment.setActive(request.getActive());
        }

        Treatment updated = treatmentRepository.save(treatment);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteTreatment(Long id) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found with id: " + id));
        treatment.setActive(false);
        treatmentRepository.save(treatment);
    }

    private TreatmentResponse mapToResponse(Treatment treatment) {
        return new TreatmentResponse(
                treatment.getId(),
                treatment.getName(),
                treatment.getDescription(),
                treatment.getPrice(),
                treatment.getDurationMinutes(),
                treatment.getImagePath(),
                treatment.isActive()
        );
    }
}
