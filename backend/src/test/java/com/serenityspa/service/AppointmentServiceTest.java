package com.serenityspa.service;

import com.serenityspa.dto.AppointmentRequest;
import com.serenityspa.dto.AppointmentResponse;
import com.serenityspa.dto.PriceCalculationResponse;
import com.serenityspa.entity.*;
import com.serenityspa.exception.ConflictException;
import com.serenityspa.repository.AppointmentRepository;
import com.serenityspa.repository.ReviewRepository;
import com.serenityspa.repository.TreatmentRepository;
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
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TreatmentRepository treatmentRepository;

    @Mock
    private MembershipService membershipService;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private User user;
    private Treatment treatment;

    @BeforeEach
    void setUp() {
        user = new User("Jane Customer", "jane@example.com", "hash", Role.ROLE_CUSTOMER);
        user.setId(10L);

        treatment = new Treatment("Relaxation Massage", "Desc", new BigDecimal("100.00"), 60, "img.jpg", true);
        treatment.setId(5L);
    }

    @Test
    void bookAppointment_Success_WithoutMembership() {
        LocalDate futureDate = LocalDate.now().plusDays(2);
        LocalTime time = LocalTime.of(14, 0);

        AppointmentRequest req = new AppointmentRequest(5L, futureDate, time, "Please quiet room");

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(treatmentRepository.findById(5L)).thenReturn(Optional.of(treatment));
        when(appointmentRepository.existsConflictingAppointment(5L, futureDate, time, AppointmentStatus.CANCELLED))
                .thenReturn(false);
        when(membershipService.getActiveMembership(user.getId())).thenReturn(Optional.empty());

        Appointment savedAppointment = new Appointment(
                user, treatment, futureDate, time, AppointmentStatus.CONFIRMED, "Please quiet room",
                new BigDecimal("100.00"), BigDecimal.ZERO, false, BigDecimal.ZERO, new BigDecimal("100.00")
        );
        savedAppointment.setId(100L);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);
        when(reviewRepository.findByAppointmentId(100L)).thenReturn(Optional.empty());

        AppointmentResponse resp = appointmentService.bookAppointment("jane@example.com", req);

        assertNotNull(resp);
        assertEquals(100L, resp.getId());
        assertEquals(AppointmentStatus.CONFIRMED, resp.getStatus());
        assertEquals("Relaxation Massage", resp.getTreatmentName());
        assertEquals(new BigDecimal("100.00"), resp.getOriginalPrice());
        assertEquals(new BigDecimal("100.00"), resp.getFinalPrice());
        assertFalse(resp.isMembershipDiscountApplied());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void bookAppointment_Success_WithActiveMembership_10PercentDiscount() {
        LocalDate futureDate = LocalDate.now().plusDays(2);
        LocalTime time = LocalTime.of(14, 0);

        AppointmentRequest req = new AppointmentRequest(5L, futureDate, time, "Lavender oil please");

        MembershipPlan plan = new MembershipPlan("Essential Wellness", "Desc", new BigDecimal("99.00"), 30, new BigDecimal("10.00"), "Benefits", true);
        UserMembership membership = new UserMembership(user, plan, LocalDate.now().minusDays(2), LocalDate.now().plusDays(28), MembershipStatus.ACTIVE);

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(treatmentRepository.findById(5L)).thenReturn(Optional.of(treatment));
        when(appointmentRepository.existsConflictingAppointment(5L, futureDate, time, AppointmentStatus.CANCELLED))
                .thenReturn(false);
        when(membershipService.getActiveMembership(user.getId())).thenReturn(Optional.of(membership));

        Appointment savedAppointment = new Appointment(
                user, treatment, futureDate, time, AppointmentStatus.CONFIRMED, "Lavender oil please",
                new BigDecimal("100.00"), new BigDecimal("10.00"), true, new BigDecimal("10.00"), new BigDecimal("90.00")
        );
        savedAppointment.setId(101L);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);
        when(reviewRepository.findByAppointmentId(101L)).thenReturn(Optional.empty());

        AppointmentResponse resp = appointmentService.bookAppointment("jane@example.com", req);

        assertNotNull(resp);
        assertEquals(new BigDecimal("100.00"), resp.getOriginalPrice());
        assertEquals(new BigDecimal("10.00"), resp.getDiscountAmount());
        assertEquals(new BigDecimal("90.00"), resp.getFinalPrice());
        assertTrue(resp.isMembershipDiscountApplied());
    }

    @Test
    void calculatePrice_ReflectsActiveMembershipDiscount() {
        MembershipPlan plan = new MembershipPlan("Essential Wellness", "Desc", new BigDecimal("99.00"), 30, new BigDecimal("10.00"), "Benefits", true);
        UserMembership membership = new UserMembership(user, plan, LocalDate.now().minusDays(1), LocalDate.now().plusDays(29), MembershipStatus.ACTIVE);

        when(treatmentRepository.findById(5L)).thenReturn(Optional.of(treatment));
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(membershipService.getActiveMembership(user.getId())).thenReturn(Optional.of(membership));

        PriceCalculationResponse priceResp = appointmentService.calculatePrice("jane@example.com", 5L);

        assertNotNull(priceResp);
        assertEquals(new BigDecimal("100.00"), priceResp.getOriginalPrice());
        assertEquals(new BigDecimal("10.00"), priceResp.getDiscountAmount());
        assertEquals(new BigDecimal("90.00"), priceResp.getFinalPrice());
        assertTrue(priceResp.isMembershipActive());
    }

    @Test
    void bookAppointment_ThrowsConflict_WhenSlotAlreadyBooked() {
        LocalDate futureDate = LocalDate.now().plusDays(2);
        LocalTime time = LocalTime.of(14, 0);

        AppointmentRequest req = new AppointmentRequest(5L, futureDate, time, null);

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(treatmentRepository.findById(5L)).thenReturn(Optional.of(treatment));
        when(appointmentRepository.existsConflictingAppointment(5L, futureDate, time, AppointmentStatus.CANCELLED))
                .thenReturn(true);

        assertThrows(ConflictException.class, () -> appointmentService.bookAppointment("jane@example.com", req));
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void bookAppointment_ThrowsException_WhenDateInPast() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        LocalTime time = LocalTime.of(10, 0);

        AppointmentRequest req = new AppointmentRequest(5L, pastDate, time, null);

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(treatmentRepository.findById(5L)).thenReturn(Optional.of(treatment));

        assertThrows(IllegalArgumentException.class, () -> appointmentService.bookAppointment("jane@example.com", req));
    }
}
