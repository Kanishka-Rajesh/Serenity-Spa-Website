package com.serenityspa.dto;

import com.serenityspa.entity.MembershipStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UserMembershipResponse {
    private Long id;
    private Long planId;
    private String planName;
    private BigDecimal price;
    private BigDecimal discountPercentage;
    private String benefits;
    private LocalDate startDate;
    private LocalDate endDate;
    private MembershipStatus status;
    private boolean activeNow;
    // Admin-visible fields (null for customer-facing responses)
    private String customerName;
    private String customerEmail;

    public UserMembershipResponse() {}

    // Constructor used by customer-facing MembershipService
    public UserMembershipResponse(Long id, Long planId, String planName, BigDecimal price,
                                  BigDecimal discountPercentage, String benefits,
                                  LocalDate startDate, LocalDate endDate,
                                  MembershipStatus status, boolean activeNow) {
        this(id, planId, planName, price, discountPercentage, benefits,
             startDate, endDate, status, activeNow, null, null);
    }

    // Constructor used by admin-facing AdminController
    public UserMembershipResponse(Long id, Long planId, String planName, BigDecimal price,
                                  BigDecimal discountPercentage, String benefits,
                                  LocalDate startDate, LocalDate endDate,
                                  MembershipStatus status, boolean activeNow,
                                  String customerName, String customerEmail) {
        this.id = id;
        this.planId = planId;
        this.planName = planName;
        this.price = price;
        this.discountPercentage = discountPercentage;
        this.benefits = benefits;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.activeNow = activeNow;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }

    public String getBenefits() { return benefits; }
    public void setBenefits(String benefits) { this.benefits = benefits; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public MembershipStatus getStatus() { return status; }
    public void setStatus(MembershipStatus status) { this.status = status; }

    public boolean isActiveNow() { return activeNow; }
    public void setActiveNow(boolean activeNow) { this.activeNow = activeNow; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
}
