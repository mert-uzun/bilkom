package com.bilkom.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bilkom.R;
import com.bilkom.adapter.ClubActivityAdapter;
import com.bilkom.model.Club;
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

public class ClubActivitiesActivity extends BaseActivity {
    private RecyclerView clubActivitiesRecyclerView;
    private Spinner clubSpinner;
    private Button addClubActivityButton;
    private Button myClubsButton;
    private Button myActivitiesButton;
    private Button createClubButton;
    private ClubActivityAdapter clubActivityAdapter;
    private List<Event> clubActivities;
    private List<Club> myClubs;
    private List<String> clubNames;
    private ApiService apiService;
    private SecureStorage secureStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_activities);
        setupNavigationDrawer();

        initializeViews();
        setupRecyclerView();
        setupSpinner();
        setupButtons();
        fetchAllClubs();
    }

    private void initializeViews() {
        clubActivitiesRecyclerView = findViewById(R.id.clubActivitiesRecyclerView);
        clubSpinner = findViewById(R.id.clubSpinner);
        addClubActivityButton = findViewById(R.id.addClubActivityButton);
        myClubsButton = findViewById(R.id.myClubsButton);
        myActivitiesButton = findViewById(R.id.myActivitiesButton);
        createClubButton = findViewById(R.id.createClubButton);
        secureStorage = new SecureStorage(this);
        apiService = RetrofitClient.getInstance().getApiService();
        clubActivities = new ArrayList<>();
        myClubs = new ArrayList<>();
        clubNames = new ArrayList<>();
    }

    private void setupRecyclerView() {
        clubActivityAdapter = new ClubActivityAdapter(this, clubActivities, this::onClubActivityClick);
        clubActivitiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        clubActivitiesRecyclerView.setAdapter(clubActivityAdapter);
    }

    private void setupSpinner() {
        clubNames.clear();
        clubNames.add("Select a club"); // Placeholder

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, clubNames) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((android.widget.TextView) view).setTextColor(android.graphics.Color.BLACK);
                return view;
            }
            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                ((android.widget.TextView) view).setTextColor(android.graphics.Color.BLACK);
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clubSpinner.setAdapter(adapter);

        clubSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    fetchAllClubActivities();
                } else if (position > 0 && position <= myClubs.size()) {
                    Club selectedClub = myClubs.get(position - 1);
                    fetchClubActivitiesByClub(selectedClub.getId());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupButtons() {
        addClubActivityButton.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(this, Class.forName("com.bilkom.ui.AddClubActivity"));
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                Log.e("ClubActivitiesActivity", "Error navigating to AddClubActivity: " + e.getMessage());
                Toast.makeText(ClubActivitiesActivity.this, "Cannot open add club activity page", Toast.LENGTH_SHORT).show();
            }
        });

        myClubsButton.setOnClickListener(v -> {
            fetchMyClubsActivities();
        });
        
        myActivitiesButton.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(this, Class.forName("com.bilkom.ui.MyActivitiesActivity"));
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                Log.e("ClubActivitiesActivity", "Error navigating to MyActivitiesActivity: " + e.getMessage());
                Toast.makeText(ClubActivitiesActivity.this, "Cannot open my activities page", Toast.LENGTH_SHORT).show();
            }
        });
        
        createClubButton.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(this, CreateClubActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.e("ClubActivitiesActivity", "Error navigating to CreateClubActivity: " + e.getMessage());
                Toast.makeText(ClubActivitiesActivity.this, "Cannot open create club page", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAllClubs() {
        apiService.listClubs().enqueue(new Callback<List<Club>>() {
            @Override
            public void onResponse(@NonNull Call<List<Club>> call, @NonNull Response<List<Club>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    myClubs.clear();
                    clubNames.clear();
                    myClubs.addAll(response.body());
                    clubNames.add("Select a club");
                    for (Club club : myClubs) {
                        clubNames.add(club.getName());
                    }
                    ((ArrayAdapter) clubSpinner.getAdapter()).notifyDataSetChanged();
                    fetchAllClubActivities(); // Optionally show all activities by default
                } else {
                    Toast.makeText(ClubActivitiesActivity.this,
                            "Failed to load clubs: " + response.message(),
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Club>> call, @NonNull Throwable t) {
                Toast.makeText(ClubActivitiesActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchClubActivitiesByClub(long clubId) {
        String token = secureStorage.getAuthToken();
        if (token == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast loadingToast = Toast.makeText(ClubActivitiesActivity.this, "Loading club activities...", Toast.LENGTH_SHORT);
        loadingToast.show();

        apiService.getClubEventsByClubId(clubId, "Bearer " + token).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
                loadingToast.cancel();
                if (response.isSuccessful() && response.body() != null) {
                    clubActivities.clear();
                    clubActivities.addAll(response.body());
                    clubActivityAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ClubActivitiesActivity.this, 
                            "Failed to load activities: " + response.message(), 
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                loadingToast.cancel();
                Toast.makeText(ClubActivitiesActivity.this, 
                        "Error: " + t.getMessage(), 
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMyClubsActivities() {
        String token = secureStorage.getAuthToken();
        if (token == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast loadingToast = Toast.makeText(this, "Loading my clubs activities...", Toast.LENGTH_SHORT);
        loadingToast.show();

        apiService.getMyClubsEvents("Bearer " + token).enqueue(new Callback<Map<Long, List<Event>>>() {
            @Override
            public void onResponse(@NonNull Call<Map<Long, List<Event>>> call, 
                                 @NonNull Response<Map<Long, List<Event>>> response) {
                loadingToast.cancel();
                if (response.isSuccessful() && response.body() != null) {
                    clubActivities.clear();
                    for (List<Event> events : response.body().values()) {
                        clubActivities.addAll(events);
                    }
                    clubActivityAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ClubActivitiesActivity.this, 
                            "Failed to load activities: " + response.message(), 
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<Long, List<Event>>> call, @NonNull Throwable t) {
                loadingToast.cancel();
                Toast.makeText(ClubActivitiesActivity.this, 
                        "Error: " + t.getMessage(), 
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAllClubActivities() {
        String token = secureStorage.getAuthToken();
        if (token == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast loadingToast = Toast.makeText(ClubActivitiesActivity.this, "Loading all club activities...", Toast.LENGTH_SHORT);
        loadingToast.show();

        apiService.getEvents("Bearer " + token).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
                loadingToast.cancel();
                if (response.isSuccessful() && response.body() != null) {
                    clubActivities.clear();
                    for (Event event : response.body()) {
                        if (event.isClubEvent()) {
                            clubActivities.add(event);
                        }
                    }
                    clubActivityAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ClubActivitiesActivity.this,
                            "Failed to load activities: " + response.message(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                loadingToast.cancel();
                Toast.makeText(ClubActivitiesActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onClubActivityClick(Event event) {
        try {
            Intent intent = new Intent(this, Class.forName("com.bilkom.ui.EventDetailsActivity"));
            intent.putExtra("event", event);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            Log.e("ClubActivitiesActivity", "Error navigating to EventDetailsActivity: " + e.getMessage());
            Toast.makeText(ClubActivitiesActivity.this, "Cannot open event details page", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            navigateToMainActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        navigateToMainActivity();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
} 