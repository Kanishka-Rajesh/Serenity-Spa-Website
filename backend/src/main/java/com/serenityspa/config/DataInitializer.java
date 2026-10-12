package com.serenityspa.config;

import com.serenityspa.entity.*;
import com.serenityspa.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TreatmentRepository treatmentRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final UserMembershipRepository userMembershipRepository;
    private final AppointmentRepository appointmentRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           TreatmentRepository treatmentRepository,
                           MembershipPlanRepository membershipPlanRepository,
                           UserMembershipRepository userMembershipRepository,
                           AppointmentRepository appointmentRepository,
                           ReviewRepository reviewRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.treatmentRepository = treatmentRepository;
        this.membershipPlanRepository = membershipPlanRepository;
        this.userMembershipRepository = userMembershipRepository;
        this.appointmentRepository = appointmentRepository;
        this.reviewRepository = reviewRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Seed Admin user
        User admin = userRepository.findByEmail("admin@serenityspa.com")
                .orElseGet(() -> userRepository.save(new User(
                        "Serenity Spa Admin",
                        "admin@serenityspa.com",
                        passwordEncoder.encode("Admin@123"),
                        Role.ROLE_ADMIN
                )));

        // Seed Sample Customer user
        User customer = userRepository.findByEmail("guest@serenityspa.com")
                .orElseGet(() -> userRepository.save(new User(
                        "Sarah Jenkins",
                        "guest@serenityspa.com",
                        passwordEncoder.encode("Guest@123"),
                        Role.ROLE_CUSTOMER
                )));

        // Seed Treatments if empty
        if (treatmentRepository.count() == 0) {
            List<Treatment> initialTreatments = List.of(
                    new Treatment(
                            "Luxury Massage Therapy",
                            "Swedish relaxation, deep tissue, hot stone, and aromatherapy massages to relieve tension and restore balance.",
                            new BigDecimal("120.00"),
                            60,
                            "https://images.unsplash.com/photo-1519415510236-718bdfcd89c8?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80",
                            true
                    ),
                    new Treatment(
                            "Advanced Facial Treatments",
                            "Hydrating oxygen, anti-aging collagen, and vitamin C brightening facials designed to rejuvenate and revitalize skin.",
                            new BigDecimal("150.00"),
                            75,
                            "https://images.unsplash.com/photo-1570172619644-dfd03ed5d881?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80",
                            true
                    ),
                    new Treatment(
                            "Holistic Body Rituals",
                            "Full body exfoliation, detox body wraps, and Ayurvedic renewal therapies for complete physical and mental revival.",
                            new BigDecimal("180.00"),
                            90,
                            "https://images.squarespace-cdn.com/content/v1/582c39e5579fb3800810246e/1486675869104-AG2VY4QN7FMD3AW2FJ20/image-asset.jpeg?format=1500w",
                            true
                    ),
                    new Treatment(
                            "Signature Wellness Combination Package",
                            "Comprehensive sanctuary experience combining custom full body massage with an organic rejuvenating facial.",
                            new BigDecimal("250.00"),
                            120,
                            "https://images.unsplash.com/photo-1540555700478-4be289fbecef?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80",
                            true
                    )
            );
            treatmentRepository.saveAll(initialTreatments);
        }

        // Seed Membership Plans matching the original website pricing and benefits
        if (membershipPlanRepository.count() == 0) {
            MembershipPlan essential = new MembershipPlan(
                    "Essential Wellness",
                    "Monthly sanctuary access with a complimentary massage, consultation, and treatment discounts.",
                    new BigDecimal("99.00"),
                    30,
                    new BigDecimal("10.00"), // Configurable 10% discount on treatments
                    "1 Massage per Month, 10% Off Additional Services, Free Wellness Consultation, Monthly Newsletter",
                    true
            );

            MembershipPlan holistic = new MembershipPlan(
                    "Holistic Harmony",
                    "The ultimate comprehensive wellness experience with priority booking and elevated discounts.",
                    new BigDecimal("199.00"),
                    30,
                    new BigDecimal("20.00"), // Configurable 20% discount on treatments
                    "2 Massages per Month, 1 Facial Treatment, 20% Off Additional Services, Free Wellness Consultation, Priority Booking, Quarterly Wellness Workshop",
                    true
            );

            membershipPlanRepository.saveAll(List.of(essential, holistic));
        }

        // Seed 1 completed appointment with genuine review if none exists (for demonstration of completed flow)
        if (reviewRepository.count() == 0 && treatmentRepository.count() > 0) {
            Treatment massage = treatmentRepository.findAll().get(0);
            Appointment completedAppt = new Appointment(
                    customer,
                    massage,
                    LocalDate.now().minusDays(5),
                    LocalTime.of(11, 0),
                    AppointmentStatus.COMPLETED,
                    "Completed previous visit",
                    massage.getPrice(),
                    BigDecimal.ZERO,
                    false,
                    BigDecimal.ZERO,
                    massage.getPrice()
            );
            Appointment savedAppt = appointmentRepository.save(completedAppt);

            Review review = new Review(
                    savedAppt,
                    customer,
                    massage,
                    5,
                    "The most incredible massage experience I've ever had. Truly revitalizing, the therapist focused on all the right pressure points."
            );
            reviewRepository.save(review);
        }
    }
}
