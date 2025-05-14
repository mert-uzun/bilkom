package com.bilkom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bilkom.R;
import com.bilkom.adapter.EventAdapter;
import com.bilkom.model.Event;
import com.bilkom.network.ApiService;
import com.bilkom.network.RetrofitClient;
import com.bilkom.utils.SecureStorage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.stream.Collectors;

public class EventActivity extends BaseActivity {
    private RecyclerView eventRecyclerView;
    private Button addActivityButton;
    private Button myActivitiesButton;
    private Spinner tagSpinner;
    private EventAdapter adapter;
    private List<Event> eventList;
    private SecureStorage secureStorage;
    private boolean isTagSpinnerInitialized = false;
    private String selectedTag = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        setupNavigationDrawer();

        eventRecyclerView = findViewById(R.id.eventRecyclerView);
        addActivityButton = findViewById(R.id.addActivityButton);
        myActivitiesButton = findViewById(R.id.myActivitiesButton);
        tagSpinner = findViewById(R.id.tagSpinner);
        secureStorage = new SecureStorage(this);

        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        adapter = new EventAdapter(this, eventList, event -> {
            Toast.makeText(this, "Join clicked for: " + event.getEventName(), Toast.LENGTH_SHORT).show();
        });
        
        // Set click listener for item click to navigate to details
        adapter.setOnItemClickListener(event -> {
            // Navigate to details activity
            Intent intent = new Intent(this, Class.forName("com.bilkom.ui.EventDetailsActivity"));
            intent.putExtra("event", event);
            startActivity(intent);
        });
        
        eventRecyclerView.setAdapter(adapter);

        addActivityButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventActivity.this, Class.forName("com.bilkom.ui.CreateEventActivity"));
            startActivity(intent);
        });
        
        // Set click listener for My Activities button
        myActivitiesButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventActivity.this, Class.forName("com.bilkom.ui.MyActivitiesActivity"));
            startActivity(intent);
        });

        tagSpinner.setSelection(0);
        tagSpinner.setOnTouchListener((v, event) -> {
            isTagSpinnerInitialized = true;
            return false;
        });
        tagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isTagSpinnerInitialized) return;
                String tag = parent.getItemAtPosition(position).toString();
                if (tag.equals("Select Tag")) {
                    selectedTag = null;
                    fetchEvents();
                } else {
                    selectedTag = tag;
                    fetchEventsByTag(selectedTag);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        fetchEvents();
    }

    private void fetchEvents() {
        fetchAllEventsNotJoined();
    }

    private void fetchAllEventsNotJoined() {
        Toast loadingToast = Toast.makeText(this, "Loading events...", Toast.LENGTH_SHORT);
        loadingToast.show();
        String token = secureStorage.getAuthToken();
        ApiService apiService = RetrofitClient.getInstance().getApiService();
        apiService.getEvents("Bearer " + token).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Event> allEvents = response.body().stream()
                            .filter(event -> !event.isClubEvent())
                            .collect(Collectors.toList());
                    // Fetch joined events
                    apiService.getJoinedEvents("Bearer " + token).enqueue(new Callback<List<Event>>() {
                        @Override
                        public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                List<Event> joinedEvents = response.body();
                                // Filter out joined events
                                List<Event> filteredEvents = allEvents.stream()
                                        .filter(event -> !joinedEvents.stream()
                                                .anyMatch(joined -> joined.getEventId().equals(event.getEventId())))
                                        .collect(Collectors.toList());
                                // Update UI
                                adapter.setEventList(filteredEvents);
                            }
                            loadingToast.cancel();
                        }

                        @Override
                        public void onFailure(Call<List<Event>> call, Throwable t) {
                            loadingToast.cancel();
                            Toast.makeText(EventActivity.this, 
                                "Error loading joined events: " + t.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    loadingToast.cancel();
                    Toast.makeText(EventActivity.this, 
                        "Failed to load events", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                loadingToast.cancel();
                Toast.makeText(EventActivity.this, 
                    "Error loading events: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchEventsByTag(String tag) {
        Toast loadingToast = Toast.makeText(this, "Loading events by tag...", Toast.LENGTH_SHORT);
        loadingToast.show();
        String token = secureStorage.getAuthToken();
        ApiService apiService = RetrofitClient.getInstance().getApiService();
        List<String> tagList = new ArrayList<>();
        tagList.add(tag);
        apiService.filterEventsByTags(tagList, "Bearer " + token).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Event> filteredEvents = response.body().stream()
                            .filter(event -> !event.isClubEvent())
                            .collect(Collectors.toList());
                    // Fetch joined events
                    apiService.getJoinedEvents("Bearer " + token).enqueue(new Callback<List<Event>>() {
                        @Override
                        public void onResponse(Call<List<Event>> call2, Response<List<Event>> response2) {
                            loadingToast.cancel();
                            Set<Long> joinedIds = new HashSet<>();
                            if (response2.isSuccessful() && response2.body() != null) {
                                for (Event joinedEvent : response2.body()) {
                                    joinedIds.add(joinedEvent.getEventId());
                                }
                            }
                            List<Event> notJoined = new ArrayList<>();
                            for (Event event : filteredEvents) {
                                if (!joinedIds.contains(event.getEventId())) {
                                    notJoined.add(event);
                                }
                            }
                            adapter.setEventList(notJoined);
                        }
                        @Override
                        public void onFailure(Call<List<Event>> call2, Throwable t2) {
                            loadingToast.cancel();
                            Toast.makeText(EventActivity.this, "Network error: " + t2.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    loadingToast.cancel();
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