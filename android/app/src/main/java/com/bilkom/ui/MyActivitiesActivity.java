package com.bilkom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bilkom.R;
import com.bilkom.adapter.EventAdapter;
import com.bilkom.adapter.CurrentEventAdapter;
import com.bilkom.adapter.PastEventAdapter;
import com.bilkom.model.Event;
import com.bilkom.network.ApiService;
import com.bilkom.network.RetrofitClient;
import com.bilkom.utils.SecureStorage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyActivitiesActivity extends BaseActivity {
    private RecyclerView currentActivitiesRecyclerView;
    private RecyclerView myClubsActivitiesRecyclerView;
    private RecyclerView pastActivitiesRecyclerView;
    
    private CurrentEventAdapter currentActivitiesAdapter;
    private EventAdapter myClubsActivitiesAdapter;
    private PastEventAdapter pastActivitiesAdapter;
    
    private List<Event> currentActivities = new ArrayList<>();
    private List<Event> myClubsActivities = new ArrayList<>();
    private List<Event> pastActivities = new ArrayList<>();
    
    private SecureStorage secureStorage;
    private ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_activities);
        setupNavigationDrawer();

        // Initialize views
        currentActivitiesRecyclerView = findViewById(R.id.currentActivitiesRecyclerView);
        myClubsActivitiesRecyclerView = findViewById(R.id.myClubsActivitiesRecyclerView);
        pastActivitiesRecyclerView = findViewById(R.id.pastActivitiesRecyclerView);

        // Initialize services
        secureStorage = new SecureStorage(this);
        apiService = RetrofitClient.getInstance().getApiService();

        // Setup RecyclerViews
        setupRecyclerViews();

        // Fetch data
        fetchCurrentActivities();
        fetchMyClubsActivities();
        fetchPastActivities();
    }

    private void setupRecyclerViews() {
        // Current Activities
        currentActivitiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        currentActivitiesAdapter = new CurrentEventAdapter(this, currentActivities, event -> {
            // Withdraw from event
            withdrawFromEvent(event);
        });
        currentActivitiesRecyclerView.setAdapter(currentActivitiesAdapter);

        // My Clubs Activities
        myClubsActivitiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myClubsActivitiesAdapter = new EventAdapter(this, myClubsActivities, null); // No click listener
        myClubsActivitiesRecyclerView.setAdapter(myClubsActivitiesAdapter);

        // Past Activities
        pastActivitiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pastActivitiesAdapter = new PastEventAdapter(this, pastActivities, event -> {
            // Report event
            reportEvent(event);
        });
        pastActivitiesRecyclerView.setAdapter(pastActivitiesAdapter);
    }

    private void fetchCurrentActivities() {
        String token = secureStorage.getAuthToken();
        Toast loadingToast = Toast.makeText(this, "Loading current activities...", Toast.LENGTH_SHORT);
        loadingToast.show();

        apiService.getJoinedEvents("Bearer " + token).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                loadingToast.cancel();
                if (response.isSuccessful() && response.body() != null) {
                    currentActivities.clear();
                    currentActivities.addAll(response.body());
                    currentActivitiesAdapter.setEventList(currentActivities);
                } else {
                    Toast.makeText(MyActivitiesActivity.this, 
                        "Failed to load current activities", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                loadingToast.cancel();
                Toast.makeText(MyActivitiesActivity.this, 
                    "Error loading current activities: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMyClubsActivities() {
        String token = secureStorage.getAuthToken();
        Toast loadingToast = Toast.makeText(this, "Loading clubs activities...", Toast.LENGTH_SHORT);
        loadingToast.show();

        apiService.getMyClubsEvents("Bearer " + token).enqueue(new Callback<Map<Long, List<Event>>>() {
            @Override
            public void onResponse(Call<Map<Long, List<Event>>> call, Response<Map<Long, List<Event>>> response) {
                loadingToast.cancel();
                if (response.isSuccessful() && response.body() != null) {
                    myClubsActivities.clear();
                    // Flatten the map into a single list
                    for (List<Event> events : response.body().values()) {
                        myClubsActivities.addAll(events);
                    }
                    myClubsActivitiesAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MyActivitiesActivity.this, 
                        "Failed to load clubs activities", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<Long, List<Event>>> call, Throwable t) {
                loadingToast.cancel();
                Toast.makeText(MyActivitiesActivity.this, 
                    "Error loading clubs activities: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPastActivities() {
        String token = secureStorage.getAuthToken();
        Toast loadingToast = Toast.makeText(this, "Loading past activities...", Toast.LENGTH_SHORT);
        loadingToast.show();

        apiService.getMyJoinedPast().enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                loadingToast.cancel();
                if (response.isSuccessful() && response.body() != null) {
                    pastActivities.clear();
                    pastActivities.addAll(response.body());
                    pastActivitiesAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MyActivitiesActivity.this, 
                        "Failed to load past activities", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                loadingToast.cancel();
                Toast.makeText(MyActivitiesActivity.this, 
                    "Error loading past activities: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void withdrawFromEvent(Event event) {
        String token = secureStorage.getAuthToken();
        Toast loadingToast = Toast.makeText(this, "Withdrawing from event...", Toast.LENGTH_SHORT);
        loadingToast.show();

        apiService.withdrawEvent(event.getEventId(), "Bearer " + token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                loadingToast.cancel();
                if (response.isSuccessful()) {
                    Toast.makeText(MyActivitiesActivity.this, 
                        "Successfully withdrew from event", Toast.LENGTH_SHORT).show();
                    fetchCurrentActivities(); 
                } else {
                    Toast.makeText(MyActivitiesActivity.this, 
                        "Failed to withdraw from event", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                loadingToast.cancel();
                Toast.makeText(MyActivitiesActivity.this, 
                    "Error withdrawing from event: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reportEvent(Event event) {
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra("eventId", event.getEventId());
        startActivity(intent);
    }
} 