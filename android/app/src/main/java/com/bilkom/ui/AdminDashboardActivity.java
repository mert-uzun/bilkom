package com.bilkom.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.bilkom.R;

/**
 * Activity for the admin dashboard. Extends {@link AppCompatActivity} to provide
 * functionality for navigating to various admin-related features.
 * Handles UI interactions for accessing user lists and club approval sections.
 * 
 * @author Sıla Bozkurt
 */
public class AdminDashboardActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_admin_dashboard);

        findViewById(R.id.btnUserList)
            .setOnClickListener(v -> startActivity(new Intent(this, AdminUserListActivity.class)));
        findViewById(R.id.btnClubApprovals)
            .setOnClickListener(v -> startActivity(new Intent(this, AdminClubApprovalActivity.class)));
    }
}