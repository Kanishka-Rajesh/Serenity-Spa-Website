package com.serenityspa.dto;

import jakarta.validation.constraints.NotNull;

public class MembershipPurchaseRequest {
    @NotNull(message = "Membership Plan ID is required")
    private Long planId;

    // Simulation / demo activation flag
    private boolean demoActivation = true;

    public MembershipPurchaseRequest() {}

    public MembershipPurchaseRequest(Long planId, boolean demoActivation) {
        this.planId = planId;
        this.demoActivation = demoActivation;
    }

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }

    public boolean isDemoActivation() { return demoActivation; }
    public void setDemoActivation(boolean demoActivation) { this.demoActivation = demoActivation; }
}
