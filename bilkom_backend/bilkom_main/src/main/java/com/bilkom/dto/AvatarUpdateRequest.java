package com.bilkom.dto;

/**
 * DTO for avatar update requests.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
public class AvatarUpdateRequest {
    
    private String avatarPath;
    
    /**
     * Empty constructor for AvatarUpdateRequest.
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public AvatarUpdateRequest() {
    }
    
    /**
     * Constructor for AvatarUpdateRequest.
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public AvatarUpdateRequest(String avatarPath) {
        this.avatarPath = avatarPath;
    }
    
    public String getAvatarPath() {
        return avatarPath;
    }
    
    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }
} 