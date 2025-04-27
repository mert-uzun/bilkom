package com.bilkom.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ClubRegistrationRequestDTO {
    @NotBlank(message = "Club name cannot be blank")
    @Size(min = 3, max = 255, message = "Club name must be between 3 and 255 characters")
    private String clubName;
    
    @NotBlank(message = "Club description cannot be blank")
    @Size(min = 10, max = 3000, message = "Club description must be between 10 and 2000 characters")
    private String clubDescription;
    
    @NotNull(message = "User ID of one executive is required")
    private Long executiveUserId;
    
    @NotBlank(message = "Position of the executive cannot be blank")
    private String executivePosition;
    
    @NotBlank(message = "Verification document URL cannot be blank")
    private String verificationDocumentUrl;
    
    // Optional additional information
    private String additionalInfo;
    
    /**
     * Default constructor for ClubRegistrationRequestDTO.
     * @author Mert Uzun
     * @version 1.0.0
     */
    public ClubRegistrationRequestDTO() {
    }
    
    /**
     * Constructor with all fields.
     * @param clubName Club name.
     * @param clubDescription Club description.
     * @param executiveUserId User ID of one executive.
     * @param executivePosition Position of the executive.
     * @param verificationDocumentUrl Verification document URL.
     * @param additionalInfo Additional information.
     * @author Mert Uzun
     * @version 1.0.0
     */
    public ClubRegistrationRequestDTO(String clubName, String clubDescription, Long executiveUserId, String executivePosition,String verificationDocumentUrl, String additionalInfo) {
        this.clubName = clubName;
        this.clubDescription = clubDescription;
        this.executiveUserId = executiveUserId;
        this.executivePosition = executivePosition;
        this.verificationDocumentUrl = verificationDocumentUrl;
        this.additionalInfo = additionalInfo;
    }
    
    // GETTERS AND SETTERS
    public String getClubName() {
        return clubName;
    }
    
    public void setClubName(String clubName) {
        this.clubName = clubName;
    }
    
    public String getClubDescription() {
        return clubDescription;
    }
    
    public void setClubDescription(String clubDescription) {
        this.clubDescription = clubDescription;
    }
    
    public Long getExecutiveUserId() {
        return executiveUserId;
    }
    
    public void setExecutiveUserId(Long executiveUserId) {
        this.executiveUserId = executiveUserId;
    }
    
    public String getExecutivePosition() {
        return executivePosition;
    }
    
    public void setExecutivePosition(String executivePosition) {
        this.executivePosition = executivePosition;
    }
    
    public String getVerificationDocumentUrl() {
        return verificationDocumentUrl;
    }
    
    public void setVerificationDocumentUrl(String verificationDocumentUrl) {
        this.verificationDocumentUrl = verificationDocumentUrl;
    }
    
    public String getAdditionalInfo() {
        return additionalInfo;
    }
    
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
