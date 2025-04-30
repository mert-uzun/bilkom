package com.bilkom.dto;

import com.bilkom.enums.AvatarRelativePaths;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for listing all available avatars.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
public class AvatarListResponse {
    
    private List<AvatarInfo> avatars;
    
    /**
     * Constructor for AvatarListResponse.
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public AvatarListResponse() {
        avatars = new ArrayList<>();
        
        // Populate with all available avatars
        for (AvatarRelativePaths path : AvatarRelativePaths.values()) {
            avatars.add(new AvatarInfo(path.name(), path.getPath()));
        }
    }
    
    /**
     * Getter for avatars.
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<AvatarInfo> getAvatars() {
        return avatars;
    }
    
    /**
     * Setter for avatars.
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public void setAvatars(List<AvatarInfo> avatars) {
        this.avatars = avatars;
    }
    
    /**
     * Inner class representing avatar information.
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public static class AvatarInfo {
        private String name;
        private String url;
        
        public AvatarInfo(String name, String url) {
            this.name = name;
            this.url = url;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getUrl() {
            return url;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }
    }
} 