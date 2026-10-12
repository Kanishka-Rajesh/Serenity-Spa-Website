package com.serenityspa.dto;

import java.math.BigDecimal;

public class PriceCalculationResponse {
    private Long treatmentId;
    private String treatmentName;
    private BigDecimal originalPrice;
    private boolean membershipActive;
    private String membershipPlanName;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private BigDecimal finalPrice;

    public PriceCalculationResponse() {}

    public PriceCalculationResponse(Long treatmentId, String treatmentName, BigDecimal originalPrice,
                                    boolean membershipActive, String membershipPlanName,
                                    BigDecimal discountPercentage, BigDecimal discountAmount,
                                    BigDecimal finalPrice) {
        this.treatmentId = treatmentId;
        this.treatmentName = treatmentName;
        this.originalPrice = originalPrice;
        this.membershipActive = membershipActive;
        this.membershipPlanName = membershipPlanName;
        this.discountPercentage = discountPercentage;
        this.discountAmount = discountAmount;
        this.finalPrice = finalPrice;
    }

    public Long getTreatmentId() { return treatmentId; }
    public void setTreatmentId(Long treatmentId) { this.treatmentId = treatmentId; }

    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }

    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }

    public boolean isMembershipActive() { return membershipActive; }
    public void setMembershipActive(boolean membershipActive) { this.membershipActive = membershipActive; }

    public String getMembershipPlanName() { return membershipPlanName; }
    public void setMembershipPlanName(String membershipPlanName) { this.membershipPlanName = membershipPlanName; }

    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getFinalPrice() { return finalPrice; }
    public void setFinalPrice(BigDecimal finalPrice) { this.finalPrice = finalPrice; }
}
