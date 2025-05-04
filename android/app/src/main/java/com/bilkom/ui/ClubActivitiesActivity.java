package com.bilkom.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bilkom.R;
import com.bilkom.adapter.ClubActivityAdapter;
import com.bilkom.model.Club;
import com.bilkom.model.Event;
import com.bilkom.network.ApiService;
import com.bilkom.network.RetrofitClient;
import com.bilkom.storage.SecureStorage;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.stream.Collectors;
import java.util.Map;

public class ClubActivitiesActivity extends BaseActivity {
    private RecyclerView clubActivitiesRecyclerView;
    private ClubActivityAdapter clubActivityAdapter;
    private Spinner clubSpinner;
    private Button addClubActivityButton;
    private Button myClubsButton;
    private SecureStorage secureStorage;
    private List<Event> clubActivities = new ArrayList<>();
    private List<Club> clubs = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_activities);
        setupNavigationDrawer();
        initializeViews();
        setupRecyclerView();
        setupClubSpinner();
        setupAddClubActivityButton();
        fetchClubs();
        fetchAllClubActivities();
    }

    private void initializeViews() {
        clubActivitiesRecyclerView = findViewById(R.id.clubActivitiesRecyclerView);
        clubSpinner = findViewById(R.id.clubSpinner);
        addClubActivityButton = findViewById(R.id.addClubActivityButton);
        myClubsButton = findViewById(R.id.myClubsButton);
        secureStorage = new SecureStorage(this);

        myClubsButton.setOnClickListener(v -> fetchMyClubsActivities());
    }

    private void setupRecyclerView() {
        clubActivityAdapter = new ClubActivityAdapter(clubActivities, this::onClubActivityClick);
        clubActivitiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        clubActivitiesRecyclerView.setAdapter(clubActivityAdapter);
    }

    private void setupClubSpinner() {
        // Add "All Clubs" option
        List<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("All Clubs");
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clubSpinner.setAdapter(adapter);

        clubSpinner.setOnItemSelectedListener((parent, view, position, id) -> {
            String selectedItem = parent.getItemAtPosition(position).toString();
            if (selectedItem.equals("All Clubs")) {
                fetchAllClubActivities();
            } else {
                // Filter by selected club
                Club selectedClub = clubs.get(position - 1); // -1 because of "All Clubs"
                fetchClubActivitiesByClub(selectedClub.getClubId());
            }
        });
    }

    private void setupAddClubActivityButton() {
        addClubActivityButton.setOnClickListener(v -> {
            // TODO: Navigate to AddClubActivity
            Toast.makeText(this, "Add Club Activity clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchClubs() {
        String token = secureStorage.getAuthToken();
        ApiService apiService = RetrofitClient.getInstance().getApiService();
        apiService.getMyClubs("Bearer " + token).enqueue(new Callback<List<Club>>() {
            @Override
            public void onResponse(Call<List<Club>> call, Response<List<Club>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    clubs = response.body();
                    updateClubSpinner();
                }
            }

            @Override
            public void onFailure(Call<List<Club>> call, Throwable t) {
                Toast.makeText(ClubActivitiesActivity.this, 
                    "Error loading clubs: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateClubSpinner() {
        List<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("All Clubs");
        for (Club club : clubs) {
            spinnerItems.add(club.getClubName());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clubSpinner.setAdapter(adapter);
    }

    private void fetchAllClubActivities() {
        Toast loadingToast = Toast.makeText(this, "Loading club activities...", Toast.LENGTH_SHORT);
        loadingToast.show();
        String token = secureStorage.getAuthToken();
        ApiService apiService = RetrofitClient.getInstance().getApiService();

        // First get all club events
        apiService.getAllClubEvents("Bearer " + token).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Event> allEvents = response.body();
                    // Then get joined events to filter them out
                    apiService.getJoinedEvents("Bearer " + token).enqueue(new Callback<List<Event>>() {
                        @Override
                        public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                            loadingToast.cancel();
                            if (response.isSuccessful() && response.body() != null) {
                                List<Event> joinedEvents = response.body();
                                // Filter out joined events
                                List<Event> unjoinedEvents = allEvents.stream()
                                    .filter(event -> !joinedEvents.stream()
                                        .anyMatch(joined -> joined.getEventId().equals(event.getEventId())))
                                    .collect(Collectors.toList());
                                updateClubActivitiesList(unjoinedEvents);
                            } else {
                                Toast.makeText(ClubActivitiesActivity.this, 
                                    "Failed to load joined events", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Event>> call, Throwable t) {
                            loadingToast.cancel();
                            Toast.makeText(ClubActivitiesActivity.this, 
                                "Error loading joined events: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    loadingToast.cancel();
                    Toast.makeText(ClubActivitiesActivity.this, 
                        "Failed to load club activities", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                loadingToast.cancel();
                Toast.makeText(ClubActivitiesActivity.this, 
                    "Error loading club activities: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMyClubsActivities() {
        Toast loadingToast = Toast.makeText(this, "Loading my clubs activities...", Toast.LENGTH_SHORT);
        loadingToast.show();
        String token = secureStorage.getAuthToken();
        ApiService apiService = RetrofitClient.getInstance().getApiService();
        apiService.getMyClubsEvents("Bearer " + token).enqueue(new Callback<Map<Long, List<Event>>>() {
            @Override
            public void onResponse(Call<Map<Long, List<Event>>> call, Response<Map<Long, List<Event>>> response) {
                loadingToast.cancel();
                if (response.isSuccessful() && response.body() != null) {
                    // Flatten the map into a single list
                    List<Event> allEvents = response.body().values().stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
                    updateClubActivitiesList(allEvents);
                } else {
                    Toast.makeText(ClubActivitiesActivity.this, 
                        "Failed to load my clubs activities", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<Long, List<Event>>> call, Throwable t) {
                loadingToast.cancel();
                Toast.makeText(ClubActivitiesActivity.this, 
                    "Error loading my clubs activities: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchClubActivitiesByClub(Long clubId) {
        Toast loadingToast = Toast.makeText(this, "Loading club activities...", Toast.LENGTH_SHORT);
        loadingToast.show();
        String token = secureStorage.getAuthToken();
        ApiService apiService = RetrofitClient.getInstance().getApiService();

        // First get club events
        apiService.getClubEventsByClubId(clubId, "Bearer " + token).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Event> clubEvents = response.body();
                    // Then get joined events to filter them out
                    apiService.getJoinedEvents("Bearer " + token).enqueue(new Callback<List<Event>>() {
                        @Override
                        public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                            loadingToast.cancel();
                            if (response.isSuccessful() && response.body() != null) {
                                List<Event> joinedEvents = response.body();
                                // Filter out joined events
                                List<Event> unjoinedEvents = clubEvents.stream()
                                    .filter(event -> !joinedEvents.stream()
                                        .anyMatch(joined -> joined.getEventId().equals(event.getEventId())))
                                    .collect(Collectors.toList());
                                updateClubActivitiesList(unjoinedEvents);
                            } else {
                                Toast.makeText(ClubActivitiesActivity.this, 
                                    "Failed to load joined events", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Event>> call, Throwable t) {
                            loadingToast.cancel();
                            Toast.makeText(ClubActivitiesActivity.this, 
                                "Error loading joined events: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    loadingToast.cancel();
                    Toast.makeText(ClubActivitiesActivity.this, 
                        "Failed to load club activities", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                loadingToast.cancel();
                Toast.makeText(ClubActivitiesActivity.this, 
                    "Error loading club activities: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateClubActivitiesList(List<Event> newActivities) {
        clubActivities.clear();
        clubActivities.addAll(newActivities);
        if (clubActivities.isEmpty()) {
            // Show empty state
            Toast.makeText(this, "No club activities found", Toast.LENGTH_SHORT).show();
        }
        clubActivityAdapter.notifyDataSetChanged();
    }

    private void onClubActivityClick(Event event) {
        // TODO: Navigate to event details
        Toast.makeText(this, "Clicked on: " + event.getEventName(), Toast.LENGTH_SHORT).show();
    }
} 