package com.serenityspa.controller;

import com.serenityspa.dto.ReviewResponse;
import com.serenityspa.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reviews")
@Tag(name = "Admin Reviews", description = "Endpoints for admin to view all customer reviews")
public class AdminReviewController {

    private final ReviewService reviewService;

    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    @Operation(summary = "Get all customer reviews (admin)")
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        // Reuse the public method which returns all reviews ordered by creation date
        return ResponseEntity.ok(reviewService.getAllPublicReviews());
    }
}
