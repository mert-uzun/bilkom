// this class is used to store the response from the server
// it is used to store the success, message, token and userId from the server 
// for example this is a pojo class just like other pojo classes in the model package
package com.bilkom.model;

public class AuthResponse {
    private boolean success;
    private String message;
    private String token;
    private Long userId;

    public AuthResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public AuthResponse(boolean success, String message, String token, Long userId) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.userId = userId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
} 