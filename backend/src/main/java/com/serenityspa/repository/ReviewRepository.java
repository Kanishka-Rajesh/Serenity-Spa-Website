package com.serenityspa.repository;

import com.serenityspa.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByAppointmentId(Long appointmentId);

    boolean existsByAppointmentId(Long appointmentId);

    List<Review> findAllByOrderByCreatedAtDesc();

    List<Review> findByTreatmentIdOrderByCreatedAtDesc(Long treatmentId);

    List<Review> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.treatment.id = :treatmentId")
    Double getAverageRatingForTreatment(@Param("treatmentId") Long treatmentId);
}
