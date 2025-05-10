package com.bilkom.model;

import com.google.gson.annotations.SerializedName;

/**
 * ReportRequest class for reporting a user or a post
 * 
 * @author Mert Uzun
 * @version 1.0
 * @since 2025-05-09
 */
public class ReportRequest {
    @SerializedName("reason")
    private String reason;
    
    @SerializedName("details")
    private String details;
    
    @SerializedName("reporterComments")
    private String reporterComments;

    // CONSTRUCTORS
    public ReportRequest() {
    }

    public ReportRequest(String reason) {
        this.reason = reason;
    }

    public ReportRequest(String reason, String details, String reporterComments) {
        this.reason = reason;
        this.details = details;
        this.reporterComments = reporterComments;
    }

    // GETTERS AND SETTERS
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getReporterComments() {
        return reporterComments;
    }

    public void setReporterComments(String reporterComments) {
        this.reporterComments = reporterComments;
    }
} 