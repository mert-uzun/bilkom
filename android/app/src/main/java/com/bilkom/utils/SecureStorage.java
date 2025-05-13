// this is the secure storage class for the secure storage of the auth token and user id
// it is imporant because we use it in the LoginActivity and RegistrationActivity to store the auth token and user id
package com.bilkom.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class SecureStorage {
    private SharedPreferences preferences;
    private static final String FILE_NAME = "bilkom_secure_prefs";
    private static final String AUTH_TOKEN_KEY = "auth_token";
    private static final String USER_ID_KEY = "user_id";

    public SecureStorage(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            preferences = EncryptedSharedPreferences.create(
                    context,
                    FILE_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            // Fallback to regular SharedPreferences if encryption fails
            preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
    }

    public void saveAuthToken(String token) {
        preferences.edit().putString(AUTH_TOKEN_KEY, token).apply();
    }

    public String getAuthToken() {
        return preferences.getString(AUTH_TOKEN_KEY, "");
    }

    public void saveUserId(long userId) {
        preferences.edit().putLong(USER_ID_KEY, userId).apply();
    }

    public long getUserId() {
        return preferences.getLong(USER_ID_KEY, -1);
    }

    public void clearAll() {
        preferences.edit().clear().apply();
    }
} 