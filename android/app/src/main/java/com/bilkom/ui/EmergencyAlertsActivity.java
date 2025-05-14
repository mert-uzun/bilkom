package com.bilkom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bilkom.R;
import com.bilkom.adapter.EmergencyAlertAdapter;
import com.bilkom.model.EmergencyAlert;
import com.bilkom.network.ApiService;
import com.bilkom.network.RetrofitClient;
import com.bilkom.utils.SecureStorage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmergencyAlertsActivity extends BaseActivity {
    private RecyclerView alertsRecyclerView;
    private FloatingActionButton addAlertButton;
    private EmergencyAlertAdapter adapter;
    private List<EmergencyAlert> alertList = new ArrayList<>();
    private View noAlertsText;
    private ApiService apiService;
    private SecureStorage secureStorage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_alerts);
        setupNavigationDrawer();

        // Initialize views
        alertsRecyclerView = findViewById(R.id.emergencyAlertsRecyclerView);
        addAlertButton = findViewById(R.id.addEmergencyAlertButton);
        noAlertsText = findViewById(R.id.noAlertsText);

        // Initialize services
        apiService = RetrofitClient.getInstance().getApiService();
        secureStorage = new SecureStorage(this);

        // Setup RecyclerView
        alertsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EmergencyAlertAdapter(this, alertList, this::onAlertClick);
        alertsRecyclerView.setAdapter(adapter);

        // Setup add button - since we don't have a create endpoint, hide the button
        // If you want to add this functionality later, you'll need to implement it in the backend
        addAlertButton.setVisibility(View.GONE);
        
        // Fetch alerts
        fetchAlerts();
    }

    private void fetchAlerts() {
        Toast loadingToast = Toast.makeText(this, "Loading emergency alerts...", Toast.LENGTH_SHORT);
        loadingToast.show();

        // Use the available endpoint
        apiService.getAlerts().enqueue(new Callback<List<EmergencyAlert>>() {
            @Override
            public void onResponse(Call<List<EmergencyAlert>> call, Response<List<EmergencyAlert>> response) {
                loadingToast.cancel();
                if (response.isSuccessful() && response.body() != null) {
                    alertList.clear();
                    alertList.addAll(response.body());
                    adapter.setAlertList(alertList);
                    
                    // Show or hide the "no alerts" message
                    if (alertList.isEmpty()) {
                        noAlertsText.setVisibility(View.VISIBLE);
                        alertsRecyclerView.setVisibility(View.GONE);
                    } else {
                        noAlertsText.setVisibility(View.GONE);
                        alertsRecyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(EmergencyAlertsActivity.this, 
                        "Failed to load emergency alerts", Toast.LENGTH_SHORT).show();
                    noAlertsText.setVisibility(View.VISIBLE);
                    alertsRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<EmergencyAlert>> call, Throwable t) {
                loadingToast.cancel();
                Toast.makeText(EmergencyAlertsActivity.this, 
                    "Error loading emergency alerts: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                noAlertsText.setVisibility(View.VISIBLE);
                alertsRecyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void onAlertClick(EmergencyAlert alert) {
        // Show details of the alert
        // Since we don't have a dedicated details page, we'll just show a toast with more info
        String detailsMessage = alert.getTitle() + "\n" +
                "Location: " + alert.getLocation() + "\n" +
                "Status: " + (alert.isActive() ? "Active" : "Resolved") + "\n" +
                "Date: " + alert.getFormattedCreated();
                
        Toast.makeText(this, detailsMessage, Toast.LENGTH_LONG).show();
        
        // In the future, you could create an EmergencyAlertDetailsActivity
        // and navigate to it with an intent that includes the alert ID
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this screen
        fetchAlerts();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the back button in the action bar
            navigateToMainActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Override to handle the hardware back button
        navigateToMainActivity();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the back stack
        startActivity(intent);
        finish(); // Close this activity
    }
} 