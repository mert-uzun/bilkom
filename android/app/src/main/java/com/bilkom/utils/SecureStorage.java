package com.bilkom.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public class SecureStorage {
    private static final String PREF_FILE = "bilkom_secure_prefs";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_USER_ID = "user_id";
    
    private final SharedPreferences securePrefs;
    
    public SecureStorage(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            securePrefs = EncryptedSharedPreferences.create(
                    context,
                    PREF_FILE,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize secure storage", e);
        }
    }
    
    public void saveAuthToken(String token) {
        securePrefs.edit().putString(KEY_AUTH_TOKEN, token).apply();
    }
    
    public String getAuthToken() {
        return securePrefs.getString(KEY_AUTH_TOKEN, null);
    }
    
    public void saveUserId(long userId) {
        securePrefs.edit().putLong(KEY_USER_ID, userId).apply();
    }
    
    public Long getUserId() {
        return securePrefs.getLong(KEY_USER_ID, -1);
    }
    
    public void clearAll() {
        securePrefs.edit().clear().apply();
    }
} 