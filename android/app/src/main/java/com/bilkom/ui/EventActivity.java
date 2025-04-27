package com.bilkom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bilkom.BaseActivity;
import com.bilkom.R;
import com.google.android.material.navigation.NavigationView;
import com.bilkom.model.Event;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.bilkom.network.RetrofitClient;
import com.bilkom.network.ApiService;
import com.bilkom.utils.SecureStorage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.bilkom.model.User;

public class EventActivity extends BaseActivity {
    private RecyclerView eventRecyclerView;
    private Button addActivityButton, viewMyActivitiesButton;
    private ImageButton menuButton;
    private RadioGroup filterRadioGroup;
    private RadioButton radioMyInterest, radioMostRecent;
    private NavigationView navigationView;
    private EventAdapter adapter;
    private List<Event> eventList;
    private ActivityResultLauncher<Intent> createEventLauncher;
    private List<String> userInterestTags = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        setupNavigationDrawer();

        eventRecyclerView = findViewById(R.id.eventRecyclerView);
        addActivityButton = findViewById(R.id.addActivityButton);
        viewMyActivitiesButton = findViewById(R.id.viewMyActivitiesButton);
        menuButton = findViewById(R.id.menuButton);
        filterRadioGroup = findViewById(R.id.filterRadioGroup);
        radioMyInterest = findViewById(R.id.radioMyInterest);
        radioMostRecent = findViewById(R.id.radioMostRecent);
        navigationView = findViewById(R.id.navigationView);

        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        adapter = new EventAdapter(this, eventList, event -> {
            Toast.makeText(this, "Join clicked for: " + event.getEventName(), Toast.LENGTH_SHORT).show();
        });
        eventRecyclerView.setAdapter(adapter);

        // Register ActivityResultLauncher
        createEventLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Always refresh events when returning
                fetchUserInterestsAndEvents();
            }
        );

        addActivityButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventActivity.this, CreateEventActivity.class);
            createEventLauncher.launch(intent);
        });

        viewMyActivitiesButton.setOnClickListener(v -> {
            // TODO: Open My Activities Activity
        });

        menuButton.setOnClickListener(v -> {
            openDrawer();
        });

        filterRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioMyInterest) {
                fetchEventsByInterest();
            } else {
                fetchAllEvents();
            }
        });

        // Fetch user interests first, then fetch events
        fetchUserInterestsAndEvents();
    }

    private void fetchUserInterestsAndEvents() {
        SecureStorage secureStorage = new SecureStorage(this);
        Long userId = secureStorage.getUserId();
        String token = secureStorage.getAuthToken();
        ApiService apiService = RetrofitClient.getInstance().getApiService();
        if (userId == null || token == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        apiService.getUserById(userId, "Bearer " + token).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    if (user.getTags() != null) {
                        userInterestTags.clear();
                        for (Object tagObj : user.getTags()) {
                            // Defensive: tagObj may be a Tag object or String depending on backend serialization
                            if (tagObj instanceof String) {
                                userInterestTags.add((String) tagObj);
                            } else if (tagObj instanceof com.bilkom.model.Tag) {
                                userInterestTags.add(((com.bilkom.model.Tag) tagObj).getTagName());
                            }
                        }
                    }
                }
                fetchEvents();
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(EventActivity.this, "Failed to load user interests", Toast.LENGTH_SHORT).show();
                fetchEvents();
            }
        });
    }

    private void fetchEvents() {
        // Default: fetch all events (most recent)
        fetchAllEvents();
    }

    private void fetchAllEvents() {
        Toast loadingToast = Toast.makeText(this, "Loading events...", Toast.LENGTH_SHORT);
        loadingToast.show();
        SecureStorage secureStorage = new SecureStorage(this);
        String token = secureStorage.getAuthToken();
        ApiService apiService = RetrofitClient.getInstance().getApiService();
        apiService.getEvents("Bearer " + token).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                loadingToast.cancel();
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setEventList(response.body());
                } else {
                    Toast.makeText(EventActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                loadingToast.cancel();
                Toast.makeText(EventActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchEventsByInterest() {
        if (userInterestTags.isEmpty()) {
            Toast.makeText(this, "No interests set. Showing all events.", Toast.LENGTH_SHORT).show();
            fetchAllEvents();
            return;
        }
        Toast loadingToast = Toast.makeText(this, "Loading events by interest...", Toast.LENGTH_SHORT);
        loadingToast.show();
        SecureStorage secureStorage = new SecureStorage(this);
        String token = secureStorage.getAuthToken();
        ApiService apiService = RetrofitClient.getInstance().getApiService();
        apiService.filterEventsByTags(userInterestTags, "Bearer " + token).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                loadingToast.cancel();
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setEventList(response.body());
                } else {
                    Toast.makeText(EventActivity.this, "Failed to load filtered events", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                loadingToast.cancel();
                Toast.makeText(EventActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
} 