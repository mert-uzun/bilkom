package com.bilkom.model;

public class RegistrationRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String bilkentId;
    private String phoneNumber;
    private String bloodType;

    public RegistrationRequest(String email, String password, String firstName, String lastName, 
                             String bilkentId, String phoneNumber, String bloodType) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bilkentId = bilkentId;
        this.phoneNumber = phoneNumber;
        this.bloodType = bloodType;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBilkentId() {
        return bilkentId;
    }

    public void setBilkentId(String bilkentId) {
        this.bilkentId = bilkentId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }
} 