// this is the secure storage class for the secure storage of the auth token and user id
// it is imporant because we use it in the LoginActivity and RegistrationActivity to store the auth token and user id
package com.bilkom.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Secure storage for sensitive data like auth tokens and user IDs.
 * Uses EncryptedSharedPreferences to store data securely.
 * 
 * @author Mert Uzun
 * @version 1.0
 * @since 2025-05-09
 */
public class SecureStorage {
    private static final String FILE_NAME = "bilkom_secure_prefs";
    private static final String AUTH_TOKEN_KEY = "auth_token";
    private static final String USER_ID_KEY = "user_id";
    
    private final SharedPreferences preferences;

    public SecureStorage(Context context) {
        try {
            // Create or get the master key
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            // Create encrypted shared preferences
            preferences = EncryptedSharedPreferences.create(
                    context,
                    FILE_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            // If encryption fails, fall back to regular SharedPreferences
            // This is less secure but ensures the app doesn't crash
            preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
    }

    /**
     * Save the authentication token
     * @param token The token to save
     */
    public void saveAuthToken(String token) {
        preferences.edit().putString(AUTH_TOKEN_KEY, token).apply();
    }

    /**
     * Get the saved authentication token
     * @return The saved token, or empty string if not found
     */
    public String getAuthToken() {
        return preferences.getString(AUTH_TOKEN_KEY, "");
    }

    /**
     * Save the user ID
     * @param userId The user ID to save
     */
    public void saveUserId(long userId) {
        preferences.edit().putLong(USER_ID_KEY, userId).apply();
    }

    /**
     * Get the saved user ID
     * @return The saved user ID, or -1 if not found
     */
    public long getUserId() {
        return preferences.getLong(USER_ID_KEY, -1);
    }

    /**
     * Clear all saved data
     */
    public void clearAll() {
        preferences.edit().clear().apply();
    }
} 