package com.bilkom.utils;

import android.view.View;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Utility class for handling Retrofit Void callbacks
 */
public class VoidCb {
    
    /**
     * Get a standard Callback object that shows success/error toasts
     * 
     * @param view The view to use for showing toasts
     * @return A callback for handling void responses
     */
    public static Callback<Void> get(View view) {
        return new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(view.getContext(), "Operation successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(view.getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(view.getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }
    
    /**
     * Get a callback with custom success message
     * 
     * @param view The view to use for showing toasts
     * @param successMessage Custom success message
     * @return A callback for handling void responses
     */
    public static Callback<Void> get(View view, String successMessage) {
        return new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(view.getContext(), successMessage, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(view.getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(view.getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }
} 