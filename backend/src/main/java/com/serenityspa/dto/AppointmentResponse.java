package com.serenityspa.dto;

import com.serenityspa.entity.AppointmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AppointmentResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private Long treatmentId;
    private String treatmentName;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private AppointmentStatus status;
    private String notes;

    // Price details snapshot
    private BigDecimal originalPrice;
    private BigDecimal discountAmount;
    private boolean membershipDiscountApplied;
    private BigDecimal discountPercentage;
    private BigDecimal finalPrice;

    // Review status
    private boolean reviewed;
    private Long reviewId;

    private LocalDateTime createdAt;

    public AppointmentResponse() {}

    public AppointmentResponse(Long id, Long customerId, String customerName, String customerEmail,
                               Long treatmentId, String treatmentName, LocalDate appointmentDate,
                               LocalTime startTime, AppointmentStatus status, String notes,
                               BigDecimal originalPrice, BigDecimal discountAmount,
                               boolean membershipDiscountApplied, BigDecimal discountPercentage,
                               BigDecimal finalPrice, boolean reviewed, Long reviewId,
                               LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.treatmentId = treatmentId;
        this.treatmentName = treatmentName;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.status = status;
        this.notes = notes;
        this.originalPrice = originalPrice;
        this.discountAmount = discountAmount;
        this.membershipDiscountApplied = membershipDiscountApplied;
        this.discountPercentage = discountPercentage;
        this.finalPrice = finalPrice;
        this.reviewed = reviewed;
        this.reviewId = reviewId;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public Long getTreatmentId() { return treatmentId; }
    public void setTreatmentId(Long treatmentId) { this.treatmentId = treatmentId; }

    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public boolean isMembershipDiscountApplied() { return membershipDiscountApplied; }
    public void setMembershipDiscountApplied(boolean membershipDiscountApplied) { this.membershipDiscountApplied = membershipDiscountApplied; }

    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }

    public BigDecimal getFinalPrice() { return finalPrice; }
    public void setFinalPrice(BigDecimal finalPrice) { this.finalPrice = finalPrice; }

    public boolean isReviewed() { return reviewed; }
    public void setReviewed(boolean reviewed) { this.reviewed = reviewed; }

    public Long getReviewId() { return reviewId; }
    public void setReviewId(Long reviewId) { this.reviewId = reviewId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
