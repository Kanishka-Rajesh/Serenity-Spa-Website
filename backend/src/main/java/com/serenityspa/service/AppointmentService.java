package com.serenityspa.service;

import com.serenityspa.dto.AppointmentRequest;
import com.serenityspa.dto.AppointmentResponse;
import com.serenityspa.dto.PriceCalculationResponse;
import com.serenityspa.entity.*;
import com.serenityspa.exception.AccessDeniedCustomException;
import com.serenityspa.exception.ConflictException;
import com.serenityspa.exception.ResourceNotFoundException;
import com.serenityspa.repository.AppointmentRepository;
import com.serenityspa.repository.ReviewRepository;
import com.serenityspa.repository.TreatmentRepository;
import com.serenityspa.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final TreatmentRepository treatmentRepository;
    private final MembershipService membershipService;
    private final ReviewRepository reviewRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              UserRepository userRepository,
                              TreatmentRepository treatmentRepository,
                              MembershipService membershipService,
                              ReviewRepository reviewRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.treatmentRepository = treatmentRepository;
        this.membershipService = membershipService;
        this.reviewRepository = reviewRepository;
    }

    public PriceCalculationResponse calculatePrice(String userEmail, Long treatmentId) {
        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found with id: " + treatmentId));

        BigDecimal originalPrice = treatment.getPrice();
        BigDecimal discountPercentage = BigDecimal.ZERO;
        boolean membershipActive = false;
        String planName = null;

        if (userEmail != null) {
            Optional<User> userOpt = userRepository.findByEmail(userEmail);
            if (userOpt.isPresent()) {
                Optional<UserMembership> activeMem = membershipService.getActiveMembership(userOpt.get().getId());
                if (activeMem.isPresent() && activeMem.get().isCurrentlyActive()) {
                    membershipActive = true;
                    planName = activeMem.get().getMembershipPlan().getName();
                    discountPercentage = activeMem.get().getMembershipPlan().getDiscountPercentage();
                }
            }
        }

        BigDecimal discountAmount = originalPrice.multiply(discountPercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal finalPrice = originalPrice.subtract(discountAmount);

        return new PriceCalculationResponse(
                treatment.getId(),
                treatment.getName(),
                originalPrice,
                membershipActive,
                planName,
                discountPercentage,
                discountAmount,
                finalPrice
        );
    }

    @Transactional
    public AppointmentResponse bookAppointment(String userEmail, AppointmentRequest request) {
        User customer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + userEmail));

        Treatment treatment = treatmentRepository.findById(request.getTreatmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found with id: " + request.getTreatmentId()));

        if (!treatment.isActive()) {
            throw new IllegalArgumentException("Cannot book an inactive treatment");
        }

        if (request.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Appointment date cannot be in the past");
        }

        // Prevent double booking for same treatment date and time
        boolean hasConflict = appointmentRepository.existsConflictingAppointment(
                treatment.getId(),
                request.getAppointmentDate(),
                request.getStartTime(),
                AppointmentStatus.CANCELLED
        );

        if (hasConflict) {
            throw new ConflictException("The selected time slot " + request.getStartTime() + " on " +
                    request.getAppointmentDate() + " is already booked for " + treatment.getName());
        }

        // Backend authoritative price calculation based on active customer membership
        BigDecimal originalPrice = treatment.getPrice();
        BigDecimal discountPercentage = BigDecimal.ZERO;
        boolean membershipApplied = false;

        Optional<UserMembership> activeMem = membershipService.getActiveMembership(customer.getId());
        if (activeMem.isPresent() && activeMem.get().isCurrentlyActive()) {
            membershipApplied = true;
            discountPercentage = activeMem.get().getMembershipPlan().getDiscountPercentage();
        }

        BigDecimal discountAmount = originalPrice.multiply(discountPercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal finalPrice = originalPrice.subtract(discountAmount);

        Appointment appointment = new Appointment(
                customer,
                treatment,
                request.getAppointmentDate(),
                request.getStartTime(),
                AppointmentStatus.PENDING,
                request.getNotes(),
                originalPrice,
                discountAmount,
                membershipApplied,
                discountPercentage,
                finalPrice
        );

        Appointment saved = appointmentRepository.save(appointment);
        return mapToResponse(saved);
    }

    public List<AppointmentResponse> getCustomerAppointments(String userEmail) {
        User customer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + userEmail));

        return appointmentRepository.findByCustomerIdOrderByAppointmentDateDescStartTimeDesc(customer.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AppointmentResponse getAppointmentById(Long appointmentId, String userEmail) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        if (user.getRole() != Role.ROLE_ADMIN && !appointment.getCustomer().getId().equals(user.getId())) {
            throw new AccessDeniedCustomException("You do not have permission to view this appointment");
        }

        return mapToResponse(appointment);
    }

    @Transactional
    public AppointmentResponse cancelAppointment(Long appointmentId, String userEmail) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        if (user.getRole() != Role.ROLE_ADMIN && !appointment.getCustomer().getId().equals(user.getId())) {
            throw new AccessDeniedCustomException("You do not have permission to cancel this appointment");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalArgumentException("Appointment is already cancelled");
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot cancel a completed appointment");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment saved = appointmentRepository.save(appointment);
        return mapToResponse(saved);
    }

    public List<AppointmentResponse> getAllAppointmentsAdmin() {
        return appointmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponse> getPendingAppointments() {
        return appointmentRepository.findByStatusOrderByAppointmentDateAscStartTimeAsc(AppointmentStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponse updateAppointmentStatus(Long appointmentId, AppointmentStatus newStatus) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

        AppointmentStatus current = appointment.getStatus();

        // Validate transition
        boolean valid = switch (current) {
            case PENDING -> newStatus == AppointmentStatus.CONFIRMED || newStatus == AppointmentStatus.CANCELLED;
            case CONFIRMED -> newStatus == AppointmentStatus.COMPLETED || newStatus == AppointmentStatus.CANCELLED;
            case COMPLETED, CANCELLED -> false; // terminal states
        };

        if (!valid) {
            throw new IllegalArgumentException(
                "Cannot transition appointment from " + current + " to " + newStatus);
        }

        // When confirming, check for conflicting confirmed appointments
        if (newStatus == AppointmentStatus.CONFIRMED) {
            boolean conflict = appointmentRepository.existsConflictingAppointment(
                    appointment.getTreatment().getId(),
                    appointment.getAppointmentDate(),
                    appointment.getStartTime(),
                    AppointmentStatus.CANCELLED
            );
            // The query also includes the current appointment if it is not CANCELLED,
            // so subtract it: conflict is real only if another appointment (not this one) is CONFIRMED
            if (conflict) {
                // Re-check excluding this appointment
                boolean otherConflict = appointmentRepository
                        .findByTreatmentIdAndAppointmentDateAndStartTimeAndStatusNot(
                                appointment.getTreatment().getId(),
                                appointment.getAppointmentDate(),
                                appointment.getStartTime(),
                                AppointmentStatus.CANCELLED)
                        .stream()
                        .anyMatch(a -> !a.getId().equals(appointmentId)
                                && a.getStatus() == AppointmentStatus.CONFIRMED);
                if (otherConflict) {
                    throw new ConflictException(
                        "Another appointment for this time slot is already confirmed. Cannot confirm this booking.");
                }
            }
        }

        appointment.setStatus(newStatus);
        Appointment saved = appointmentRepository.save(appointment);
        return mapToResponse(saved);
    }

    private AppointmentResponse mapToResponse(Appointment a) {
        Optional<Review> rev = reviewRepository.findByAppointmentId(a.getId());
        return new AppointmentResponse(
                a.getId(),
                a.getCustomer().getId(),
                a.getCustomer().getFullName(),
                a.getCustomer().getEmail(),
                a.getTreatment().getId(),
                a.getTreatment().getName(),
                a.getAppointmentDate(),
                a.getStartTime(),
                a.getStatus(),
                a.getNotes(),
                a.getOriginalPrice(),
                a.getDiscountAmount(),
                a.isMembershipDiscountApplied(),
                a.getDiscountPercentage(),
                a.getFinalPrice(),
                rev.isPresent(),
                rev.map(Review::getId).orElse(null),
                a.getCreatedAt()
        );
    }
}
