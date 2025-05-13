package com.bilkom.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bilkom.R;
import com.bilkom.model.Event;
import com.bilkom.network.ApiService;
import com.bilkom.network.RetrofitClient;
import com.bilkom.utils.DateUtils;
import com.bilkom.utils.SecureStorage;
import com.bilkom.utils.VoidCb;
import com.bilkom.utils.ApiErrorHandler;
import com.bilkom.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment to display event details with properly formatted date
 * 
 * @author Mert Uzun
 * @version 1.0
 * @since 2025-05-09
 */
public class EventDetailsFragment extends Fragment {
    private static final String TAG = "EventDetailsFragment";
    private static final String ARG_EVENT = "event";
    
    private Event event;
    private TextView titleTextView;
    private TextView dateTextView;
    private TextView locationTextView;
    private TextView descriptionTextView;
    private TextView participantsTextView;
    private LinearLayout tagsContainer;
    private Button joinButton;
    private Button withdrawButton;
    private ApiService apiService;
    private SecureStorage secureStorage;
    private String token;
    
    /**
     * Create a new instance of the fragment with the provided event
     * 
     * @param event The event to display
     * @return A new instance of EventDetailsFragment
     */
    public static EventDetailsFragment newInstance(Event event) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && event == null) {
            event = (Event) getArguments().getSerializable(ARG_EVENT);
        } else if (savedInstanceState != null && event == null) {
            event = (Event) savedInstanceState.getSerializable(ARG_EVENT);
        }
        
        apiService = RetrofitClient.getInstance().getApiService();
        secureStorage = new SecureStorage(requireContext());
        token = secureStorage.getAuthToken();
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        
        titleTextView = view.findViewById(R.id.event_title);
        dateTextView = view.findViewById(R.id.event_date);
        locationTextView = view.findViewById(R.id.event_location);
        descriptionTextView = view.findViewById(R.id.event_description);
        participantsTextView = view.findViewById(R.id.event_participants);
        tagsContainer = view.findViewById(R.id.event_tags_container);
        joinButton = view.findViewById(R.id.join_button);
        withdrawButton = view.findViewById(R.id.withdraw_button);
        
        joinButton.setOnClickListener(v -> joinEvent());
        withdrawButton.setOnClickListener(v -> withdrawFromEvent());
        
        return view;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        displayEventDetails();
        updateJoinStatus();
    }
    
    private void displayEventDetails() {
        if (event == null) return;
        
        titleTextView.setText(event.getEventName());
        locationTextView.setText(event.getEventLocation());
        descriptionTextView.setText(event.getEventDescription());
        
        try {
            Date eventDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                .parse(event.getEventDate());
            String formattedDate = new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.US)
                .format(eventDate);
            dateTextView.setText(formattedDate);
        } catch (Exception e) {
            dateTextView.setText(event.getEventDate());
        }
        
        participantsTextView.setText(event.getCurrentParticipants() + "/" + event.getMaxParticipants());
        
        displayTags(event.getTags());
    }
    
    private void displayTags(List<String> tags) {
        tagsContainer.removeAllViews();
        if (tags == null || tags.isEmpty()) return;
        
        for (String tag : tags) {
            TextView tagView = new TextView(requireContext());
            tagView.setText(tag);
            tagView.setTextColor(Color.WHITE);
            tagView.setBackgroundResource(R.drawable.tag_chip_bg);
            tagView.setPadding(24, 8, 24, 8);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 16, 0);
            tagView.setLayoutParams(params);
            
            tagsContainer.addView(tagView);
        }
    }
    
    private void updateJoinStatus() {
        if (event == null) return;
        
        boolean isJoined = event.isJoined();
        joinButton.setVisibility(isJoined ? View.GONE : View.VISIBLE);
        withdrawButton.setVisibility(isJoined ? View.VISIBLE : View.GONE);
    }
    
    private void joinEvent() {
        joinButton.setEnabled(false);
        
        apiService.joinEvent(event.getEventId(), "Bearer " + token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                joinButton.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Successfully joined the event", Toast.LENGTH_SHORT).show();
                    event.setJoined(true);
                    event.setCurrentParticipants(event.getCurrentParticipants() + 1);
                    updateJoinStatus();
                    displayEventDetails();
                } else {
                    Toast.makeText(requireContext(), "Failed to join: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                joinButton.setEnabled(true);
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void withdrawFromEvent() {
        withdrawButton.setEnabled(false);
        
        apiService.withdrawEvent(event.getEventId(), "Bearer " + token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                withdrawButton.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Successfully withdrawn from the event", Toast.LENGTH_SHORT).show();
                    event.setJoined(false);
                    event.setCurrentParticipants(Math.max(0, event.getCurrentParticipants() - 1));
                    updateJoinStatus();
                    displayEventDetails();
                } else {
                    Toast.makeText(requireContext(), "Failed to withdraw: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                withdrawButton.setEnabled(true);
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (event != null) {
            outState.putSerializable(ARG_EVENT, event);
        }
    }
} 