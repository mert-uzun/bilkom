package com.bilkom.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bilkom.R;
import com.bilkom.model.Club;
import com.bilkom.model.Event;
import com.bilkom.model.EventRequest;
import com.bilkom.network.ApiService;
import com.bilkom.network.RetrofitClient;
import com.bilkom.utils.DateUtils;
import com.bilkom.utils.SecureStorage;

import java.util.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddClubActivity extends AppCompatActivity {
    // Declare all UI elements
    private EditText eventNameEdit, eventLocationEdit, eventDateEdit, maxParticipantsEdit, eventDescriptionEdit;
    private LinearLayout tagsContainer;
    private Button submitEventButton;
    private Spinner clubSpinner;
    
    private SecureStorage secureStorage;
    private Club selectedClub;
    private Set<String> selectedTags = new HashSet<>();
    private List<Club> myClubs = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_club);

        // Initialize views
        eventNameEdit = findViewById(R.id.eventNameEditText);
        eventLocationEdit = findViewById(R.id.eventLocationEditText);
        eventDateEdit = findViewById(R.id.eventDateEdit);
        maxParticipantsEdit = findViewById(R.id.maxParticipantsEditText);
        eventDescriptionEdit = findViewById(R.id.eventDescriptionEditText);
        tagsContainer = findViewById(R.id.tagsContainer);
        submitEventButton = findViewById(R.id.createEventButton);
        clubSpinner = findViewById(R.id.clubSpinner);

        secureStorage = new SecureStorage(this);
        
        // Setup date picker
        setupDatePicker();
        
        // Load user's clubs for the spinner
        loadUserClubs();
        
        // Setup tags
        setupTags();
        
        // Set up button click listener
        submitEventButton.setOnClickListener(v -> submitClubEvent());
    }
    
    private void loadUserClubs() {
        String token = secureStorage.getAuthToken();
        if (token.isEmpty()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        ApiService apiService = RetrofitClient.getInstance().getApiService();
        apiService.getMyClubsAll().enqueue(new Callback<List<Club>>() {
            @Override
            public void onResponse(Call<List<Club>> call, Response<List<Club>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    myClubs = response.body();
                    setupClubSpinner();
                } else {
                    Toast.makeText(AddClubActivity.this, "Failed to load clubs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Club>> call, Throwable t) {
                Toast.makeText(AddClubActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupClubSpinner() {
        List<String> clubNames = new ArrayList<>();
        for (Club club : myClubs) {
            clubNames.add(club.getName());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, clubNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clubSpinner.setAdapter(adapter);
        
        clubSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedClub = myClubs.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedClub = null;
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
        if (selectedClub == null) {
            Toast.makeText(this, "Please select a club", Toast.LENGTH_SHORT).show();
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

        // Format date for API
        String formattedDate = date; // Assume date is already in correct format, or use DateUtils if needed
        
        // Prepare request
        EventRequest request = new EventRequest(name, description, maxParticipants, location, formattedDate, tags, true, selectedClub.getId());
        
        String token = secureStorage.getAuthToken();
        ApiService apiService = RetrofitClient.getInstance().getApiService();

        Toast loadingToast = Toast.makeText(this, "Creating event...", Toast.LENGTH_SHORT);
        loadingToast.show();
        
        // Use the regular createEvent method, as it should handle club events too
        apiService.createEvent(request).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                loadingToast.cancel();
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AddClubActivity.this, "Club event created!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddClubActivity.this, "Failed to create event", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                loadingToast.cancel();
                Toast.makeText(AddClubActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * Setup date picker for event date selection
     */
    private void setupDatePicker() {
        eventDateEdit.setOnClickListener(v -> {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new DatePickerDialog and show it
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Format the date in YYYY-MM-DD format
                        String formattedDate = String.format("%04d-%02d-%02d", 
                                selectedYear, selectedMonth + 1, selectedDay);
                        eventDateEdit.setText(formattedDate);
                    },
                    year, month, day);
            
            // Set min date to today
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });
    }
    
    /**
     * Setup tags for event
     */
    private void setupTags() {
        // Sample tags (replace with dynamic tags if needed)
        List<String> tagList = new ArrayList<>();
        tagList.add("Sports");
        tagList.add("Study");
        tagList.add("Music");
        tagList.add("Art");
        tagList.add("Tech");
        tagList.add("Club");
        tagList.add("Competition");
        tagList.add("Board Games");
        tagList.add("Workshop");
        tagList.add("Social");

        tagsContainer.removeAllViews();
        for (String tag : tagList) {
            TextView tagView = new TextView(this);
            tagView.setText(tag);
            tagView.setTextColor(getResources().getColor(android.R.color.white));
            tagView.setBackgroundResource(R.drawable.tag_chip_bg);
            tagView.setPadding(24, 8, 24, 8);
            tagView.setTextSize(14);
            tagView.setClickable(true);
            tagView.setFocusable(true);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 16, 0);
            tagView.setLayoutParams(params);
            tagView.setOnClickListener(v -> {
                if (selectedTags.contains(tag)) {
                    selectedTags.remove(tag);
                    tagView.setBackgroundResource(R.drawable.tag_chip_bg);
                } else {
                    selectedTags.add(tag);
                    tagView.setBackgroundResource(R.drawable.tag_chip_selected_bg);
                }
            });
            tagsContainer.addView(tagView);
        }
    }
} 