package com.serenityspa.dto;

import java.time.LocalDateTime;

public class PublicReviewResponse {
    private Long id;
    private String fullName;
    private String profession;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    public PublicReviewResponse() {}

    public PublicReviewResponse(Long id, String fullName, String profession,
                                Integer rating, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.profession = profession;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
