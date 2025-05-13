package com.bilkom.model;

import com.google.gson.annotations.SerializedName;
import androidx.annotation.NonNull;

/**
 * Data model for club creation requests
 * 
 * @author Ali Sevindi
 * @version 1.0
 * @since 2025-05-09
 */
public class ClubRequest {
    @SerializedName("clubName")
    @NonNull
    private String clubName;
    
    @SerializedName("clubDescription")
    @NonNull
    private String clubDescription;
    
    @SerializedName("executiveUserId")
    @NonNull
    private Long executiveUserId;
    
    @SerializedName("executivePosition")
    @NonNull
    private String executivePosition;
    
    @SerializedName("verificationDocumentUrl")
    @NonNull
    private String verificationDocumentUrl;
    
    @SerializedName("additionalInfo")
    private String additionalInfo;
    
    // CONSTRUCTORS
    public ClubRequest() {
    }
    
    public ClubRequest(@NonNull String clubName, @NonNull String clubDescription, @NonNull Long executiveUserId) {
        this.clubName = clubName;
        this.clubDescription = clubDescription;
        this.executiveUserId = executiveUserId;
    }
    
    public ClubRequest(@NonNull String clubName, @NonNull String clubDescription, @NonNull Long executiveUserId, 
                      @NonNull String executivePosition, @NonNull String verificationDocumentUrl, String additionalInfo) {
        this.clubName = clubName;
        this.clubDescription = clubDescription;
        this.executiveUserId = executiveUserId;
        this.executivePosition = executivePosition;
        this.verificationDocumentUrl = verificationDocumentUrl;
        this.additionalInfo = additionalInfo;
    }

    // GETTERS AND SETTERS
    @NonNull
    public String getClubName() {
        return clubName;
    }

    public void setClubName(@NonNull String clubName) {
        this.clubName = clubName;
    }

    @NonNull
    public String getClubDescription() {
        return clubDescription;
    }

    public void setClubDescription(@NonNull String clubDescription) {
        this.clubDescription = clubDescription;
    }

    @NonNull
    public Long getExecutiveUserId() {
        return executiveUserId;
    }

    public void setExecutiveUserId(@NonNull Long executiveUserId) {
        this.executiveUserId = executiveUserId;
    }

    @NonNull
    public String getExecutivePosition() {
        return executivePosition;
    }

    public void setExecutivePosition(@NonNull String executivePosition) {
        this.executivePosition = executivePosition;
    }

    @NonNull
    public String getVerificationDocumentUrl() {
        return verificationDocumentUrl;
    }

    public void setVerificationDocumentUrl(@NonNull String verificationDocumentUrl) {
        this.verificationDocumentUrl = verificationDocumentUrl;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    /**
     * Validates the club request fields
     * @throws IllegalArgumentException if any validation fails
     */
    public void validate() {
        if (clubName == null || clubName.trim().isEmpty()) {
            throw new IllegalArgumentException("Club name cannot be blank");
        }
        if (clubName.length() < 3 || clubName.length() > 255) {
            throw new IllegalArgumentException("Club name must be between 3 and 255 characters");
        }

        if (clubDescription == null || clubDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Club description cannot be blank");
        }
        if (clubDescription.length() < 10 || clubDescription.length() > 3000) {
            throw new IllegalArgumentException("Club description must be between 10 and 3000 characters");
        }

        if (executiveUserId == null) {
            throw new IllegalArgumentException("Executive user ID cannot be null");
        }

        if (executivePosition == null || executivePosition.trim().isEmpty()) {
            throw new IllegalArgumentException("Executive position cannot be blank");
        }

        if (verificationDocumentUrl == null || verificationDocumentUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Verification document URL cannot be blank");
        }
    }
} 