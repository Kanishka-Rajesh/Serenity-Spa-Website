package com.serenityspa.controller;

import com.serenityspa.dto.MembershipPlanResponse;
import com.serenityspa.dto.MembershipPurchaseRequest;
import com.serenityspa.dto.UserMembershipResponse;
import com.serenityspa.service.MembershipService;
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
@RequestMapping("/api/memberships")
@Tag(name = "Memberships", description = "Endpoints for viewing membership plans, user membership status, and activation")
public class MembershipController {

    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @GetMapping("/plans")
    @Operation(summary = "List all active membership plans (Public)")
    public ResponseEntity<List<MembershipPlanResponse>> getMembershipPlans() {
        return ResponseEntity.ok(membershipService.getAllActivePlans());
    }

    @GetMapping("/plans/{id}")
    @Operation(summary = "Get membership plan details by ID (Public)")
    public ResponseEntity<MembershipPlanResponse> getPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(membershipService.getPlanById(id));
    }

    @GetMapping("/my")
    @Operation(summary = "Get current authenticated customer active membership")
    public ResponseEntity<UserMembershipResponse> getCurrentCustomerMembership(@AuthenticationPrincipal UserDetails userDetails) {
        return membershipService.getCurrentCustomerMembership(userDetails.getUsername())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/my/history")
    @Operation(summary = "Get current authenticated customer membership history")
    public ResponseEntity<List<UserMembershipResponse>> getCustomerMembershipHistory(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(membershipService.getCustomerMembershipHistory(userDetails.getUsername()));
    }

    @PostMapping("/activate")
    @Operation(summary = "Activate or renew membership for authenticated customer (Demo / verified flow)")
    public ResponseEntity<UserMembershipResponse> activateMembership(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody MembershipPurchaseRequest request) {
        UserMembershipResponse response = membershipService.activateMembership(userDetails.getUsername(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
