package com.serenityspa.controller;

import com.serenityspa.dto.PublicReviewRequest;
import com.serenityspa.dto.PublicReviewResponse;
import com.serenityspa.service.PublicReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public-reviews")
@Tag(name = "Public Reviews", description = "Public review form submissions (no login required)")
public class PublicReviewController {

    private final PublicReviewService publicReviewService;

    public PublicReviewController(PublicReviewService publicReviewService) {
        this.publicReviewService = publicReviewService;
    }

    /** Anyone can submit a review */
    @PostMapping
    @Operation(summary = "Submit a public review")
    public ResponseEntity<PublicReviewResponse> submit(@Valid @RequestBody PublicReviewRequest request) {
        return new ResponseEntity<>(publicReviewService.submit(request), HttpStatus.CREATED);
    }

    /** Public endpoint to read all reviews (for the testimonials section) */
    @GetMapping
    @Operation(summary = "Get all public reviews")
    public ResponseEntity<List<PublicReviewResponse>> getAll() {
        return ResponseEntity.ok(publicReviewService.getAll());
    }
}
