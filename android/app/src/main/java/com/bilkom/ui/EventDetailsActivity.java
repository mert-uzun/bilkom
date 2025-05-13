package com.bilkom.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.bilkom.R;
import com.bilkom.model.Event;

public class EventDetailsActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setupNavigationDrawer();

        // Get event from intent
        Event event = (Event) getIntent().getSerializableExtra("event");
        if (event == null) {
            finish();
            return;
        }

        // Add EventDetailsFragment
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentFrame, EventDetailsFragment.newInstance(event))
                .commit();
        }
    }
} 