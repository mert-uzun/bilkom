package com.bilkom.android.network.models;

import com.google.gson.annotations.SerializedName;

public class RegistrationRequest {
    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("bilkentId")
    private String bilkentId;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("bloodType")
    private String bloodType;


    public RegistrationRequest() {}


    public RegistrationRequest(String email, String password,
                               String firstName, String lastName,
                               String bilkentId, String phoneNumber, String bloodType) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bilkentId = bilkentId;
        this.phoneNumber = phoneNumber;
        this.bloodType = bloodType;
    }


    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getBilkentId() { return bilkentId; }
    public void setBilkentId(String bilkentId) { this.bilkentId = bilkentId; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
}