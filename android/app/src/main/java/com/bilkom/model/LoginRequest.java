//this is a pojo class for the login request 
// it is used to store the email and password from the user 
package com.bilkom.model;

import com.google.gson.annotations.SerializedName;

/**
 * Minimal credentials payload for <code>POST /auth/login</code>.
 *
 * @author Sıla Bozkurt
 */
public class LoginRequest {

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public LoginRequest() { }

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail()     { return email; }
    public void   setEmail(String email) { this.email = email; }

    public String getPassword()  { return password; }
    public void   setPassword(String password) { this.password = password; }
}
