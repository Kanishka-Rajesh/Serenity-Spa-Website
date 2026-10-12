package com.serenityspa.controller;

import com.serenityspa.dto.TreatmentRequest;
import com.serenityspa.dto.TreatmentResponse;
import com.serenityspa.service.TreatmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Treatments", description = "Endpoints for viewing and administering spa treatments")
public class TreatmentController {

    private final TreatmentService treatmentService;

    public TreatmentController(TreatmentService treatmentService) {
        this.treatmentService = treatmentService;
    }

    @GetMapping("/treatments")
    @Operation(summary = "Get all active treatments (Public)")
    public ResponseEntity<List<TreatmentResponse>> getTreatments() {
        return ResponseEntity.ok(treatmentService.getAllActiveTreatments());
    }

    @GetMapping("/treatments/{id}")
    @Operation(summary = "Get single treatment by ID (Public)")
    public ResponseEntity<TreatmentResponse> getTreatmentById(@PathVariable Long id) {
        return ResponseEntity.ok(treatmentService.getTreatmentById(id));
    }

    @PostMapping("/admin/treatments")
    @Operation(summary = "Create a new treatment (Admin only)")
    public ResponseEntity<TreatmentResponse> createTreatment(@Valid @RequestBody TreatmentRequest request) {
        TreatmentResponse response = treatmentService.createTreatment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/admin/treatments/{id}")
    @Operation(summary = "Update an existing treatment (Admin only)")
    public ResponseEntity<TreatmentResponse> updateTreatment(@PathVariable Long id, @Valid @RequestBody TreatmentRequest request) {
        return ResponseEntity.ok(treatmentService.updateTreatment(id, request));
    }

    @DeleteMapping("/admin/treatments/{id}")
    @Operation(summary = "Deactivate a treatment (Admin only)")
    public ResponseEntity<Void> deleteTreatment(@PathVariable Long id) {
        treatmentService.deleteTreatment(id);
        return ResponseEntity.noContent().build();
    }
}
