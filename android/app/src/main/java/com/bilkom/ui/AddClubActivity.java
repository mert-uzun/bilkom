package com.bilkom.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.bilkom.R;
import com.bilkom.model.Club;
import com.bilkom.model.EventRequest;
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

public class AddClubActivity extends CreateEventActivity {
    private Spinner clubSpinner;
    private List<Club> clubs = new ArrayList<>();
    private SecureStorage secureStorage;
    private ApiService apiService;
    private Set<String> selectedTags = new HashSet<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_club_activity);
        setupNavigationDrawer();

        // Initialize views
        eventNameEdit = findViewById(R.id.eventNameEditText);
        eventLocationEdit = findViewById(R.id.eventLocationEditText);
        eventDateEdit = findViewById(R.id.eventDateEdit);
        maxParticipantsEdit = findViewById(R.id.maxParticipantsEditText);
        eventDescriptionEdit = findViewById(R.id.eventDescriptionEditText);
        tagsContainer = findViewById(R.id.tagsContainer);
        submitEventButton = findViewById(R.id.createEventButton);
        clubSpinner = findViewById(R.id.clubSpinner);

        // Initialize services
        secureStorage = new SecureStorage(this);
        apiService = RetrofitClient.getInstance().getApiService();

        // Setup club spinner
        setupClubSpinner();
        fetchClubs();

        // Setup tags with default tags
        setupDefaultTags();

        // Setup submit button
        submitEventButton.setOnClickListener(v -> submitClubEvent());
    }

    private void setupClubSpinner() {
        ArrayAdapter<Club> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, clubs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clubSpinner.setAdapter(adapter);
    }

    private void fetchClubs() {
        String token = secureStorage.getAuthToken();
        apiService.getMyClubs("Bearer " + token).enqueue(new Callback<List<Club>>() {
            @Override
            public void onResponse(Call<List<Club>> call, Response<List<Club>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    clubs.clear();
                    clubs.addAll(response.body());
                    ((ArrayAdapter) clubSpinner.getAdapter()).notifyDataSetChanged();
                } else {
                    Toast.makeText(AddClubActivity.this, 
                        "Failed to load clubs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Club>> call, Throwable t) {
                Toast.makeText(AddClubActivity.this, 
                    "Error loading clubs: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitClubEvent() {
        // Validate form
        String name = eventNameEdit.getText().toString().trim();
        String location = eventLocationEdit.getText().toString().trim();
        String date = eventDateEdit.getText().toString().trim();
        String maxParticipantsStr = maxParticipantsEdit.getText().toString().trim();
        String description = eventDescriptionEdit.getText().toString().trim();
        List<String> tags = new ArrayList<>(selectedTags);

        boolean valid = true;
        if (name.isEmpty()) {
            eventNameEdit.setError("Required"); valid = false;
        }
        if (location.isEmpty()) {
            eventLocationEdit.setError("Required"); valid = false;
        }
        if (date.isEmpty()) {
            eventDateEdit.setError("Required"); valid = false;
        }
        if (maxParticipantsStr.isEmpty()) {
            maxParticipantsEdit.setError("Required"); valid = false;
        }
        if (description.isEmpty()) {
            eventDescriptionEdit.setError("Required"); valid = false;
        }
        if (tags.isEmpty()) {
            Toast.makeText(this, "Select at least one tag", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (clubSpinner.getSelectedItem() == null) {
            Toast.makeText(this, "Select a club", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        int maxParticipants = 0;
        try {
            maxParticipants = Integer.parseInt(maxParticipantsStr);
            if (maxParticipants <= 0) {
                maxParticipantsEdit.setError("Must be > 0"); valid = false;
            }
        } catch (NumberFormatException e) {
            maxParticipantsEdit.setError("Invalid number"); valid = false;
        }
        if (!valid) return;

        // Get selected club
        Club selectedClub = (Club) clubSpinner.getSelectedItem();

        // Prepare request
        EventRequest request = new EventRequest(
            name,           // eventName
            description,    // eventDescription
            maxParticipants,// maxParticipants
            location,       // eventLocation
            date,          // eventDate
            tags,          // tags
            true,          // isClubEvent
            selectedClub.getId() // clubId
        );

        String token = "Bearer " + secureStorage.getAuthToken();
        Toast loadingToast = Toast.makeText(this, "Creating club event...", Toast.LENGTH_SHORT);
        loadingToast.show();

        // Use the regular createEvent method, as it should handle club events too
        apiService.createEvent(request, token).enqueue(new Callback<com.bilkom.model.Event>() {
            @Override
            public void onResponse(Call<com.bilkom.model.Event> call, Response<com.bilkom.model.Event> response) {
                loadingToast.cancel();
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AddClubActivity.this, "Club event created!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddClubActivity.this, 
                        "Failed to create club event", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.bilkom.model.Event> call, Throwable t) {
                loadingToast.cancel();
                Toast.makeText(AddClubActivity.this, 
                    "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
} 