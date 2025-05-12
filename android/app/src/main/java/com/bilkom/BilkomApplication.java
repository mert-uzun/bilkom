package com.bilkom;

import android.app.Application;
import com.bilkom.network.RetrofitClient;

public class BilkomApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitClient.init(this);
    }
} 