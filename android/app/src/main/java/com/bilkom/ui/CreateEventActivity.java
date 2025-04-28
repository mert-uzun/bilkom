package com.bilkom.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.bilkom.BaseActivity;
import com.bilkom.R;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.bilkom.model.EventRequest;
import com.bilkom.network.ApiService;
import com.bilkom.network.RetrofitClient;
import com.bilkom.utils.SecureStorage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

            // Prepare request
            EventRequest request = new EventRequest(name, description, location, date, maxParticipants, tags);
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
} 