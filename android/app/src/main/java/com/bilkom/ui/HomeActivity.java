package com.bilkom.ui;

import android.os.Bundle;
import com.bilkom.BaseActivity;
import com.bilkom.R;

public class HomeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setupNavigationDrawer();
        
        // Inflate the home content into the content frame
        getLayoutInflater().inflate(R.layout.activity_main, findViewById(R.id.contentFrame));
    }
} 