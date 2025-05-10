package com.bilkom.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.bilkom.model.User;
import com.google.gson.Gson;

/**
 * Manages user session data and authentication state, for better user experience and programming practices
 * 
 * @author Mert Uzun
 * @version 1.0
 * @since 2025-05-09
 */
public class SessionManager {
    private static final String PREF_NAME = "BilkomSession";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER = "user_data";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private static SessionManager instance;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private final Gson gson;

    private SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Save user login session data
     * @param token JWT authentication token
     * @param userId User ID
     * @param user User object
     */
    public void createLoginSession(String token, Long userId, User user) {
        editor.putString(KEY_TOKEN, token);
        editor.putLong(KEY_USER_ID, userId);
        editor.putString(KEY_USER, gson.toJson(user));
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    /**
     * Update stored user data
     * @param user Updated user object
     */
    public void updateUserData(User user) {
        editor.putString(KEY_USER, gson.toJson(user));
        editor.apply();
    }

    /**
     * Get authentication token with "Bearer " prefix for API calls
     * @return Formatted token string for Authorization header
     */
    public String getAuthToken() {
        String token = sharedPreferences.getString(KEY_TOKEN, null);
        if (token != null && !token.startsWith("Bearer ")) {
            return "Bearer " + token;
        }
        return token;
    }

    /**
     * Get raw token without "Bearer " prefix
     * @return Raw token string
     */
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    /**
     * Get stored user ID
     * @return User ID
     */
    public Long getUserId() {
        return sharedPreferences.getLong(KEY_USER_ID, -1);
    }

    /**
     * Get stored user data
     * @return User object
     */
    public User getUser() {
        String userJson = sharedPreferences.getString(KEY_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    /**
     * Check if user is logged in
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Clear session data on logout
     */
    public void clearSession() {
        editor.clear();
        editor.apply();
    }
} 