package com.serenityspa.service;

import com.serenityspa.dto.ReviewRequest;
import com.serenityspa.dto.ReviewResponse;
import com.serenityspa.entity.*;
import com.serenityspa.exception.AccessDeniedCustomException;
import com.serenityspa.exception.ConflictException;
import com.serenityspa.exception.ResourceNotFoundException;
import com.serenityspa.repository.AppointmentRepository;
import com.serenityspa.repository.ReviewRepository;
import com.serenityspa.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         AppointmentRepository appointmentRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReviewResponse submitReview(String userEmail, ReviewRequest request) {
        User customer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + userEmail));

        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + request.getAppointmentId()));

        // Verification 1: Ownership check
        if (!appointment.getCustomer().getId().equals(customer.getId())) {
            throw new AccessDeniedCustomException("You can only review your own appointments");
        }

        // Verification 2: Status check (must be COMPLETED)
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new IllegalArgumentException("Only completed appointments can be reviewed. Current status: " + appointment.getStatus());
        }

        // Verification 3: At most one review per appointment
        if (reviewRepository.existsByAppointmentId(appointment.getId())) {
            throw new ConflictException("A review has already been submitted for this appointment");
        }

        Review review = new Review(
                appointment,
                customer,
                appointment.getTreatment(),
                request.getRating(),
                request.getComment()
        );

        Review saved = reviewRepository.save(review);
        return mapToResponse(saved);
    }

    public List<ReviewResponse> getAllPublicReviews() {
        return reviewRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ReviewResponse> getReviewsByTreatment(Long treatmentId) {
        return reviewRepository.findByTreatmentIdOrderByCreatedAtDesc(treatmentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ReviewResponse> getMyReviews(String userEmail) {
        User customer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + userEmail));

        return reviewRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Double getAverageRating(Long treatmentId) {
        Double avg = reviewRepository.getAverageRatingForTreatment(treatmentId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 5.0;
    }

    private ReviewResponse mapToResponse(Review r) {
        return new ReviewResponse(
                r.getId(),
                r.getAppointment().getId(),
                r.getCustomer().getId(),
                r.getCustomer().getFullName(),
                r.getTreatment().getId(),
                r.getTreatment().getName(),
                r.getRating(),
                r.getComment(),
                r.getCreatedAt()
        );
    }
}
