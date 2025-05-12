package com.bilkom.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import com.bilkom.utils.DateUtils;

/**
 * Server response returned by <code>POST&nbsp;/auth/login</code> and
 * <code>POST&nbsp;/auth/register</code>. Holds the JWT that the client
 * must send as <kbd>Authorization: Bearer&nbsp;&lt;token&gt;</kbd> in
 * subsequent requests.
 *
 * Use {@link #getToken()} to persist the JWT (e.g., via
 * {@code TokenProvider.saveToken()}), and {@link #getExpiresDate()}
 * if you need to schedule a proactive refresh.
 *
 * @author  Sıla Bozkurt
 * @version 1.0
 * @since   2025-05-11
 */
public class AuthResponse {

    @SerializedName("token")        private String token;
    @SerializedName("refreshToken") private String refreshToken;
    @SerializedName("expiresAt")    private String expiresAt; 
    @SerializedName("userId")       private Long   userId;
    @SerializedName("role")         private String role;

    public AuthResponse() { }

    public AuthResponse(String token, String refreshToken,
                        String expiresAt, Long userId, String role) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
        this.userId = userId;
        this.role = role;
    }


    public String getToken()         { return token; }
    public void   setToken(String t) { this.token = t; }

    public String getRefreshToken()          { return refreshToken; }
    public void   setRefreshToken(String rt) { this.refreshToken = rt; }

    public String getExpiresAt()          { return expiresAt; }
    public void   setExpiresAt(String ea) { this.expiresAt = ea; }

    public Long   getUserId()        { return userId; }
    public void   setUserId(Long id) { this.userId = id; }

    public String getRole()          { return role; }
    public void   setRole(String r)  { this.role = r; }

    public Date getExpiresDate() {
        return DateUtils.parseApiDate(expiresAt);
    }
}
