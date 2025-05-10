package com.bilkom.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.bilkom.network.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Utility class for handling API errors and standardizing error handling across the app for better error handling
 *  
 * @author Mert Uzun
 * @version 1.0
 * @since 2025-05-09
 */
public class ApiErrorHandler {
    private static final String TAG = "ApiErrorHandler";

    /**
     * Generic callback for handling API responses with standardized error handling
     * @param <T> The type of the response body
     * 
     * @author Mert Uzun
     * @version 1.0
     * @since 2025-05-09
     */
    public static class ApiCallback<T> implements Callback<T> {
        private final Context context;
        private final OnSuccess<T> onSuccess;
        private final OnError onError;
        private final String errorMessage;

        /**
         * Create a new ApiCallback
         * @param context The activity or fragment context
         * @param onSuccess Callback for successful API response
         * @param onError Callback for error (optional)
         * @param errorMessage Default error message to show
         *  
         * @author Mert Uzun
         * @version 1.0
         * @since 2025-05-09
         */
        public ApiCallback(Context context, OnSuccess<T> onSuccess, OnError onError, String errorMessage) {
            this.context = context;
            this.onSuccess = onSuccess;
            this.onError = onError;
            this.errorMessage = errorMessage;
        }

        /**
         * Handles successful API responses
         * @param call The API call
         * @param response The API response
         * 
         * @author Mert Uzun
         * @version 1.0
         * @since 2025-05-09
         */
        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if (response.isSuccessful() && response.body() != null) {
                onSuccess.onSuccess(response.body());
            } 
            else {
                String message = parseErrorResponse(response);
                if (message == null) {
                    message = errorMessage != null ? errorMessage : "An error occurred";
                }
                showError(message);
                
                if (onError != null) {
                    onError.onError(message, response.code());
                }
            }
        }

        /**
         * Handles API call failures
         * @param call The API call
         * @param t The throwable
         * 
         * @author Mert Uzun
         * @version 1.0
         * @since 2025-05-09
         */
        @Override
        public void onFailure(Call<T> call, Throwable t) {
            Log.e(TAG, "API call failed", t);
            String message;
            
            if (t instanceof SocketTimeoutException) {
                message = "Connection timeout. Please check your internet connection.";
            } 
            else if (t instanceof IOException) {
                message = "Network error. Please check your connection.";
            } 
            else {
                message = errorMessage != null ? errorMessage : "An unexpected error occurred";
            }
            
            showError(message);
            
            if (onError != null) {
                onError.onError(message, 0);
            }
        }
        
        private void showError(String message) {
            if (context != null) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }
        
        private String parseErrorResponse(Response<T> response) {
            try {
                if (response.errorBody() != null) {
                    String errorBody = response.errorBody().string();
                    JsonObject jsonObject = new Gson().fromJson(errorBody, JsonObject.class);
                    
                    // Try to get message from different possible formats
                    if (jsonObject.has("message")) {
                        return jsonObject.get("message").getAsString();
                    } 
                    else if (jsonObject.has("error")) {
                        return jsonObject.get("error").getAsString();
                    } 
                    else if (jsonObject.has("errorMessage")) {
                        return jsonObject.get("errorMessage").getAsString();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing error response", e);
            }
            
            // Fallback to HTTP status messages
            switch (response.code()) {
                case 400: 
                    return "Bad request. Please check your input.";
                case 401: 
                    return "Unauthorized. Please login again.";
                case 403: 
                    return "Forbidden. You don't have permission.";
                case 404: 
                    return "Resource not found.";
                case 500: 
                    return "Server error. Please try again later.";
                default: 
                    return "Error: " + response.code();
            }
        }
    }

    /**
     * Interface for successful response
     * 
     * @author Mert Uzun
     * @version 1.0
     * @since 2025-05-09
     */
    public interface OnSuccess<T> {
        void onSuccess(T response);
    }

    /**
     * Interface for error handling
     * 
     * @author Mert Uzun
     * @version 1.0
     * @since 2025-05-09
     */
    public interface OnError {
        void onError(String message, int errorCode);
    }
    
    /**
     * Handle 401 Unauthorized errors that might indicate expired token
     * @param context The activity or fragment context
     * @param errorCode The HTTP error code
     * 
     * @author Mert Uzun
     * @version 1.0
     * @since 2025-05-09
     */
    public static void handleUnauthorized(Context context, int errorCode) {
        if (errorCode == 401) {
            // Clear stored credentials and redirect to login
            SessionManager.getInstance(context).clearSession();
            
            // Show message
            Toast.makeText(context, "Session expired. Please login again.", Toast.LENGTH_LONG).show();
            
            // Navigate to login activity
            Intent intent = new Intent(context, com.bilkom.ui.LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
    }
} 