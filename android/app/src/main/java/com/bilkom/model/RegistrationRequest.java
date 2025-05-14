// this is a pojo class for the registration request 
// it is used to store the email, password, firstName, lastName, bilkentId, phoneNumber and bloodType from the user 
package com.bilkom.model;

import com.google.gson.annotations.SerializedName;

/**
 * RegistrationRequest class for user registration
 * 
 * @author Sıla Bozkurt
 * @version 1.0
 * @since 2025-05-11
 */

public class RegistrationRequest {

    @SerializedName("firstName") private String firstName;
    @SerializedName("lastName")  private String lastName;
    @SerializedName("email")     private String email;
    @SerializedName("password")  private String password;
    @SerializedName("bilkentId") private String bilkentId;
    @SerializedName("phoneNumber") private String phoneNumber;
    @SerializedName("bloodType") private String bloodType;

    public RegistrationRequest() { }

    public RegistrationRequest(String firstName, String lastName,
                             String email, String password,
                             String bilkentId, String phoneNumber,
                             String bloodType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.bilkentId = bilkentId;
        this.phoneNumber = phoneNumber;
        this.bloodType = bloodType;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getBilkentId() { return bilkentId; }
    public void setBilkentId(String bilkentId) { this.bilkentId = bilkentId; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
}