package com.serenityspa.dto;

import java.math.BigDecimal;

public class MembershipPlanResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer durationDays;
    private BigDecimal discountPercentage;
    private String benefits;
    private boolean active;

    public MembershipPlanResponse() {}

    public MembershipPlanResponse(Long id, String name, String description, BigDecimal price,
                                  Integer durationDays, BigDecimal discountPercentage,
                                  String benefits, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationDays = durationDays;
        this.discountPercentage = discountPercentage;
        this.benefits = benefits;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }

    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }

    public String getBenefits() { return benefits; }
    public void setBenefits(String benefits) { this.benefits = benefits; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
