package com.serenityspa.controller;

import com.serenityspa.dto.AppointmentResponse;
import com.serenityspa.dto.ReviewResponse;
import com.serenityspa.dto.StatusUpdateRequest;
import com.serenityspa.dto.UserMembershipResponse;
import com.serenityspa.entity.ContactEnquiry;
import com.serenityspa.entity.MembershipStatus;
import com.serenityspa.entity.UserMembership;
import com.serenityspa.exception.ResourceNotFoundException;
import com.serenityspa.repository.ContactEnquiryRepository;
import com.serenityspa.repository.UserMembershipRepository;
import com.serenityspa.service.AppointmentService;
import com.serenityspa.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Protected admin-only endpoints for the spa owner panel")
public class AdminController {

    private final AppointmentService appointmentService;
    private final ContactEnquiryRepository contactEnquiryRepository;
    private final UserMembershipRepository userMembershipRepository;
    private final ReviewService reviewService;

    public AdminController(AppointmentService appointmentService,
                           ContactEnquiryRepository contactEnquiryRepository,
                           UserMembershipRepository userMembershipRepository,
                           ReviewService reviewService) {
        this.appointmentService = appointmentService;
        this.contactEnquiryRepository = contactEnquiryRepository;
        this.userMembershipRepository = userMembershipRepository;
        this.reviewService = reviewService;
    }

    // ── Appointments ─────────────────────────────────────────────────────────

    @GetMapping("/appointments")
    @Operation(summary = "List all appointments (Admin)")
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointmentsAdmin());
    }

    @GetMapping("/appointments/pending")
    @Operation(summary = "List pending appointments awaiting confirmation (Admin)")
    public ResponseEntity<List<AppointmentResponse>> getPendingAppointments() {
        return ResponseEntity.ok(appointmentService.getPendingAppointments());
    }

    @PatchMapping("/appointments/{id}/status")
    @Operation(summary = "Confirm, complete or cancel an appointment (Admin)")
    public ResponseEntity<AppointmentResponse> updateAppointmentStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, request.getStatus()));
    }

    // ── Memberships ──────────────────────────────────────────────────────────

    @GetMapping("/memberships")
    @Operation(summary = "List all currently active customer memberships (Admin)")
    public ResponseEntity<List<UserMembershipResponse>> getActiveMemberships() {
        List<UserMembership> active = userMembershipRepository.findAllCurrentlyActive();
        List<UserMembershipResponse> response = active.stream().map(um -> new UserMembershipResponse(
                um.getId(),
                um.getMembershipPlan().getId(),
                um.getMembershipPlan().getName(),
                um.getMembershipPlan().getPrice(),
                um.getMembershipPlan().getDiscountPercentage(),
                um.getMembershipPlan().getBenefits(),
                um.getStartDate(),
                um.getEndDate(),
                um.getStatus(),
                um.isCurrentlyActive(),
                um.getUser().getFullName(),
                um.getUser().getEmail()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/memberships/{id}/deactivate")
    @Operation(summary = "Deactivate a customer membership (Admin)")
    public ResponseEntity<Map<String, String>> deactivateMembership(@PathVariable Long id) {
        UserMembership membership = userMembershipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found: " + id));
        membership.setStatus(MembershipStatus.CANCELLED);
        userMembershipRepository.save(membership);
        return ResponseEntity.ok(Map.of("message", "Membership deactivated successfully"));
    }

    // ── Contact Messages ─────────────────────────────────────────────────────

    @GetMapping("/messages")
    @Operation(summary = "List all customer contact enquiries (Admin)")
    public ResponseEntity<List<ContactEnquiry>> getContactMessages() {
        return ResponseEntity.ok(contactEnquiryRepository.findAllByOrderByCreatedAtDesc());
    }

    @PatchMapping("/messages/{id}/read")
    @Operation(summary = "Mark a contact message as read (Admin)")
    public ResponseEntity<ContactEnquiry> markMessageRead(@PathVariable Long id) {
        ContactEnquiry msg = contactEnquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found: " + id));
        msg.setRead(true);
        return ResponseEntity.ok(contactEnquiryRepository.save(msg));
    }

    // ── Reviews ──────────────────────────────────────────────────────────────

    @GetMapping("/reviews")
    @Operation(summary = "List all customer reviews (Admin)")
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllPublicReviews());
    }

    // ── Summary Stats ────────────────────────────────────────────────────────

    @GetMapping("/stats")
    @Operation(summary = "Get summary counts for the admin dashboard (Admin)")
    public ResponseEntity<Map<String, Long>> getStats() {
        long pending = appointmentService.getPendingAppointments().size();
        long activeMemberships = userMembershipRepository.findAllCurrentlyActive().size();
        long unreadMessages = contactEnquiryRepository.findAllByOrderByCreatedAtDesc()
                .stream().filter(m -> !m.isRead()).count();
        long totalReviews = reviewService.getAllPublicReviews().size();
        return ResponseEntity.ok(Map.of(
                "pendingAppointments", pending,
                "activeMemberships", activeMemberships,
                "unreadMessages", unreadMessages,
                "totalReviews", totalReviews
        ));
    }
}
