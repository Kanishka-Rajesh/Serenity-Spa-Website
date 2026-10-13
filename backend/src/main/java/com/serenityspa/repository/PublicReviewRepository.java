package com.serenityspa.repository;

import com.serenityspa.entity.PublicReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicReviewRepository extends JpaRepository<PublicReview, Long> {
    List<PublicReview> findAllByOrderByCreatedAtDesc();
}
