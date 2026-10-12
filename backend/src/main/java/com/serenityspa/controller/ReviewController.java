package com.serenityspa.controller;

import com.serenityspa.dto.ReviewRequest;
import com.serenityspa.dto.ReviewResponse;
import com.serenityspa.service.ReviewService;
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
@RequestMapping("/api/reviews")
@Tag(name = "Reviews", description = "Endpoints for public verified reviews and submitting reviews for completed treatments")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    @Operation(summary = "Get all public verified reviews (Public)")
    public ResponseEntity<List<ReviewResponse>> getAllReviews(@RequestParam(required = false) Long treatmentId) {
        if (treatmentId != null) {
            return ResponseEntity.ok(reviewService.getReviewsByTreatment(treatmentId));
        }
        return ResponseEntity.ok(reviewService.getAllPublicReviews());
    }

    @GetMapping("/my")
    @Operation(summary = "Get all reviews submitted by the currently authenticated customer")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reviewService.getMyReviews(userDetails.getUsername()));
    }

    @PostMapping
    @Operation(summary = "Submit a verified review for a completed appointment")
    public ResponseEntity<ReviewResponse> submitReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.submitReview(userDetails.getUsername(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
