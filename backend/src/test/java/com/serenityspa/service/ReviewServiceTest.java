package com.serenityspa.service;

import com.serenityspa.dto.ReviewRequest;
import com.serenityspa.dto.ReviewResponse;
import com.serenityspa.entity.*;
import com.serenityspa.exception.AccessDeniedCustomException;
import com.serenityspa.exception.ConflictException;
import com.serenityspa.repository.AppointmentRepository;
import com.serenityspa.repository.ReviewRepository;
import com.serenityspa.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private User otherUser;
    private Treatment treatment;
    private Appointment completedAppointment;
    private Appointment confirmedAppointment;

    @BeforeEach
    void setUp() {
        user = new User("Jane Customer", "jane@example.com", "hash", Role.ROLE_CUSTOMER);
        user.setId(1L);

        otherUser = new User("John Intruder", "john@example.com", "hash", Role.ROLE_CUSTOMER);
        otherUser.setId(2L);

        treatment = new Treatment("Luxury Massage", "Desc", new BigDecimal("120.00"), 60, "img.jpg", true);
        treatment.setId(10L);

        completedAppointment = new Appointment(
                user, treatment, LocalDate.now().minusDays(1), LocalTime.of(10, 0),
                AppointmentStatus.COMPLETED, "Great",
                new BigDecimal("120.00"), BigDecimal.ZERO, false, BigDecimal.ZERO, new BigDecimal("120.00")
        );
        completedAppointment.setId(100L);

        confirmedAppointment = new Appointment(
                user, treatment, LocalDate.now().plusDays(2), LocalTime.of(15, 0),
                AppointmentStatus.CONFIRMED, "Upcoming",
                new BigDecimal("120.00"), BigDecimal.ZERO, false, BigDecimal.ZERO, new BigDecimal("120.00")
        );
        confirmedAppointment.setId(101L);
    }

    @Test
    void submitReview_Success_WhenAppointmentIsCompletedAndOwnedByCustomer() {
        ReviewRequest req = new ReviewRequest(100L, 5, "Incredible massage experience!");

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(completedAppointment));
        when(reviewRepository.existsByAppointmentId(100L)).thenReturn(false);

        Review savedReview = new Review(completedAppointment, user, treatment, 5, "Incredible massage experience!");
        savedReview.setId(500L);
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        ReviewResponse resp = reviewService.submitReview("jane@example.com", req);

        assertNotNull(resp);
        assertEquals(500L, resp.getId());
        assertEquals(5, resp.getRating());
        assertEquals("Incredible massage experience!", resp.getComment());
        assertEquals("Luxury Massage", resp.getTreatmentName());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void submitReview_ThrowsAccessDenied_WhenAppointmentBelongsToDifferentCustomer() {
        ReviewRequest req = new ReviewRequest(100L, 5, "Trying to review someone else's appointment");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(otherUser));
        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(completedAppointment));

        assertThrows(AccessDeniedCustomException.class, () -> reviewService.submitReview("john@example.com", req));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void submitReview_ThrowsException_WhenAppointmentNotCompleted() {
        ReviewRequest req = new ReviewRequest(101L, 4, "Too early to review");

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(appointmentRepository.findById(101L)).thenReturn(Optional.of(confirmedAppointment));

        assertThrows(IllegalArgumentException.class, () -> reviewService.submitReview("jane@example.com", req));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void submitReview_ThrowsConflict_WhenReviewAlreadySubmitted() {
        ReviewRequest req = new ReviewRequest(100L, 5, "Duplicate review attempt");

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(completedAppointment));
        when(reviewRepository.existsByAppointmentId(100L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> reviewService.submitReview("jane@example.com", req));
        verify(reviewRepository, never()).save(any(Review.class));
    }
}
