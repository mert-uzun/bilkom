package com.bilkom.ui;

import android.os.Bundle;
import com.bilkom.BaseActivity;
import com.bilkom.R;

public class ProfileActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setupNavigationDrawer();
        
        // Inflate the profile content into the content frame
        getLayoutInflater().inflate(R.layout.activity_profile, findViewById(R.id.contentFrame));
    }
} 