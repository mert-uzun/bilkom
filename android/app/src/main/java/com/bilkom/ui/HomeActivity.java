// this is the home activity class for the home page
// it is used to store the home page content
// it extends the BaseActivity class to show the menu 
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