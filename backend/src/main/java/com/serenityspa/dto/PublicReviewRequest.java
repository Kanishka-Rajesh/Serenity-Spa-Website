package com.serenityspa.dto;

import jakarta.validation.constraints.*;

public class PublicReviewRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 200)
    private String fullName;

    @Size(max = 200)
    private String profession;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @NotBlank(message = "Comment is required")
    @Size(max = 2000)
    private String comment;

    public PublicReviewRequest() {}

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
