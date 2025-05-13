package com.bilkom.model;

import com.google.gson.annotations.SerializedName;

public class ApiError {
    @SerializedName("message")
    private String message;
    
    @SerializedName("status")
    private int status;
    
    @SerializedName("timestamp")
    private String timestamp;
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
} 