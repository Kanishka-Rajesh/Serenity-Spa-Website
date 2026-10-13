package com.serenityspa.service;

import com.serenityspa.dto.PublicReviewRequest;
import com.serenityspa.dto.PublicReviewResponse;
import com.serenityspa.entity.PublicReview;
import com.serenityspa.repository.PublicReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicReviewService {

    private final PublicReviewRepository publicReviewRepository;

    public PublicReviewService(PublicReviewRepository publicReviewRepository) {
        this.publicReviewRepository = publicReviewRepository;
    }

    @Transactional
    public PublicReviewResponse submit(PublicReviewRequest request) {
        PublicReview review = new PublicReview(
                request.getFullName(),
                request.getProfession(),
                request.getRating(),
                request.getComment()
        );
        PublicReview saved = publicReviewRepository.save(review);
        return toResponse(saved);
    }

    public List<PublicReviewResponse> getAll() {
        return publicReviewRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private PublicReviewResponse toResponse(PublicReview r) {
        return new PublicReviewResponse(
                r.getId(),
                r.getFullName(),
                r.getProfession(),
                r.getRating(),
                r.getComment(),
                r.getCreatedAt()
        );
    }
}
