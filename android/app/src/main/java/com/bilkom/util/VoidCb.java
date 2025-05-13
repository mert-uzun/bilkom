package com.bilkom.util;

import android.view.View;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Utility class for handling void callbacks
 */
public class VoidCb {
    public static Callback<Void> get(View v) {
        return new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    // Handle error
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure
            }
        };
    }
} 