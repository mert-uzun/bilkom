package com.bilkom.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.Nullable;
import com.bilkom.BaseActivity;
import com.bilkom.R;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import java.util.*;
import com.bilkom.model.EventRequest;
import com.bilkom.network.*;
import com.bilkom.utils.*;
import retrofit2.*;

public class CreateEventActivity extends BaseActivity {
    private EditText eventNameEdit, eventLocationEdit, eventDateEdit, maxParticipantsEdit, eventDescriptionEdit;
    private LinearLayout tagsContainer;
    private Button submitEventButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        setupNavigationDrawer();

        eventNameEdit = findViewById(R.id.eventNameEdit);
        eventLocationEdit = findViewById(R.id.eventLocationEdit);
        eventDateEdit = findViewById(R.id.eventDateEdit);
        maxParticipantsEdit = findViewById(R.id.maxParticipantsEdit);
        eventDescriptionEdit = findViewById(R.id.eventDescriptionEdit);
        tagsContainer = findViewById(R.id.tagsContainer);
        submitEventButton = findViewById(R.id.submitEventButton);
        
        // Set up date picker for eventDateEdit
        setupDatePicker();

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

        Set<String> selectedTags = new HashSet<>();
        tagsContainer.removeAllViews();
        for (String tag : tagList) {
            TextView tagView = new TextView(this);
            tagView.setText(tag);
            tagView.setTextColor(Color.WHITE);
            tagView.setBackgroundResource(R.drawable.tag_chip_bg); // Use your chip drawable
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
                    tagView.setBackgroundResource(R.drawable.tag_chip_selected_bg); // Use a different drawable for selected
                }
            });
            tagsContainer.addView(tagView);
        }

        submitEventButton.setOnClickListener(v -> {
            // Validate form
            String name = eventNameEdit.getText().toString().trim();
            String location = eventLocationEdit.getText().toString().trim();
            String dateStr = eventDateEdit.getText().toString().trim();
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
            if (dateStr.isEmpty()) {
                eventDateEdit.setError("Required"); valid = false;
            } else if (!DateUtils.isValidDateFormat(dateStr)) {
                eventDateEdit.setError("Invalid date format (YYYY-MM-DD)"); valid = false;
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
            Date eventDate = DateUtils.parseUserInputDate(dateStr);
            String formattedDate = DateUtils.formatApiDate(eventDate);
            
            // Prepare request
            EventRequest request = new EventRequest(name, description, maxParticipants, location, formattedDate, tags, false, null);
            SecureStorage secureStorage = new SecureStorage(this);
            String token = secureStorage.getAuthToken();
            ApiService apiService = RetrofitClient.getInstance().getApiService();

            Toast loadingToast = Toast.makeText(this, "Creating event...", Toast.LENGTH_SHORT);
            loadingToast.show();
            apiService.createEvent(request, "Bearer " + token).enqueue(new Callback<com.bilkom.model.Event>() {
                @Override
                public void onResponse(Call<com.bilkom.model.Event> call, Response<com.bilkom.model.Event> response) {
                    loadingToast.cancel();
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(CreateEventActivity.this, "Event created!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CreateEventActivity.this, "Failed to create event", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<com.bilkom.model.Event> call, Throwable t) {
                    loadingToast.cancel();
                    Toast.makeText(CreateEventActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    
    /**
     * Set up a date picker dialog for the event date field
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
} 