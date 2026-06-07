package com.jeevadaana.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Used during post-camp management to record a donation for a registered donor.
 */
public class RecordDonationForm {

    @NotNull(message = "Registration is required")
    private Long registrationId;

    @Min(value = 100, message = "Units must be at least 100 ml")
    private Integer unitsMl = 450;

    private String remarks;

    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }

    public Integer getUnitsMl() {
        return unitsMl;
    }

    public void setUnitsMl(Integer unitsMl) {
        this.unitsMl = unitsMl;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
