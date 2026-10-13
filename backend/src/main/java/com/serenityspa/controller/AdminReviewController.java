package com.serenityspa.controller;

import com.serenityspa.dto.PublicReviewResponse;
import com.serenityspa.service.PublicReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reviews")
@Tag(name = "Admin Reviews", description = "Endpoints for admin to view all public reviews submitted via the review form")
public class AdminReviewController {

    private final PublicReviewService publicReviewService;

    public AdminReviewController(PublicReviewService publicReviewService) {
        this.publicReviewService = publicReviewService;
    }

    @GetMapping
    @Operation(summary = "Get all public reviews submitted via the review form (admin)")
    public ResponseEntity<List<PublicReviewResponse>> getAllReviews() {
        return ResponseEntity.ok(publicReviewService.getAll());
    }
}
