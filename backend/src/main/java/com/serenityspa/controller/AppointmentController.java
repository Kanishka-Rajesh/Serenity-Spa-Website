package com.serenityspa.controller;

import com.serenityspa.dto.AppointmentRequest;
import com.serenityspa.dto.AppointmentResponse;
import com.serenityspa.dto.PriceCalculationResponse;
import com.serenityspa.dto.StatusUpdateRequest;
import com.serenityspa.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Appointments", description = "Endpoints for scheduling, viewing, cancelling and updating appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/appointments/calculate-price")
    @Operation(summary = "Calculate price preview for a treatment including active membership discount")
    public ResponseEntity<PriceCalculationResponse> calculatePrice(
            @RequestParam Long treatmentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(appointmentService.calculatePrice(email, treatmentId));
    }

    @PostMapping("/appointments")
    @Operation(summary = "Book a new appointment (Customer)")
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AppointmentRequest request) {
        AppointmentResponse response = appointmentService.bookAppointment(userDetails.getUsername(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/appointments/my")
    @Operation(summary = "Get all appointments for the logged-in customer")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(appointmentService.getCustomerAppointments(userDetails.getUsername()));
    }

    @GetMapping("/appointments/{id}")
    @Operation(summary = "Get appointment details by ID (Customer owner or Admin)")
    public ResponseEntity<AppointmentResponse> getAppointmentById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id, userDetails.getUsername()));
    }

    @PatchMapping("/appointments/{id}/cancel")
    @Operation(summary = "Cancel an appointment (Customer owner or Admin)")
    public ResponseEntity<AppointmentResponse> cancelAppointment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id, userDetails.getUsername()));
    }

    // Admin endpoints moved to AdminController to avoid mapping conflicts.
    // Keep this file focused on customer-facing appointment operations.

}
