package com.bilkom.ui;

import android.os.Bundle;
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
import com.bilkom.utils.ApiErrorHandler;
import com.bilkom.utils.SessionManager;

/**
 * Fragment to display event details with properly formatted date
 * 
 * @author Mert Uzun
 * @version 1.0
 * @since 2025-05-09
 */
public class EventDetailsFragment extends Fragment {
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable(ARG_EVENT);
        }
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
        
        return view;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        if (event != null) {
            titleTextView.setText(event.getEventName());
            
            // Use the formatted date from DateUtils
            dateTextView.setText(event.getFormattedEventDateTime());
            
            locationTextView.setText(event.getEventLocation());
            descriptionTextView.setText(event.getEventDescription());
            
            // Show participants count
            String participantsText = String.format("Participants: %d/%d", 
                    event.getCurrentParticipantsNumber(), 
                    event.getMaxParticipants());
            participantsTextView.setText(participantsText);
            
            // Add event tags
            if (event.getTags() != null && !event.getTags().isEmpty()) {
                tagsContainer.removeAllViews();
                for (String tag : event.getTags()) {
                    TextView tagView = new TextView(requireContext());
                    tagView.setText(tag);
                    tagView.setPadding(16, 8, 16, 8);
                    tagView.setBackgroundResource(R.drawable.tag_chip_bg);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 16, 0);
                    tagView.setLayoutParams(params);
                    tagsContainer.addView(tagView);
                }
            }
            
            // Set up join/withdraw buttons
            setupButtons();
        }
    }
    
    private void setupButtons() {
        ApiService apiService = RetrofitClient.getInstance().getApiService();
        String token = SessionManager.getInstance(requireContext()).getAuthToken();
        
        joinButton.setOnClickListener(v -> {
            apiService.joinEvent(event.getEventId(), "Bearer " + token)
                    .enqueue(new ApiErrorHandler.ApiCallback<>(
                            requireContext(),
                            response -> {
                                Toast.makeText(requireContext(), "Successfully joined event", Toast.LENGTH_SHORT).show();
                                // Update UI
                                joinButton.setVisibility(View.GONE);
                                withdrawButton.setVisibility(View.VISIBLE);
                            },
                            (message, errorCode) -> {
                                // Handle error
                                ApiErrorHandler.handleUnauthorized(requireContext(), errorCode);
                            },
                            "Failed to join event"
                    ));
        });
        
        withdrawButton.setOnClickListener(v -> {
            apiService.withdrawFromEvent(event.getEventId(), "Bearer " + token)
                    .enqueue(new ApiErrorHandler.ApiCallback<>(
                            requireContext(),
                            response -> {
                                Toast.makeText(requireContext(), "Successfully withdrew from event", Toast.LENGTH_SHORT).show();
                                // Update UI
                                joinButton.setVisibility(View.VISIBLE);
                                withdrawButton.setVisibility(View.GONE);
                            },
                            (message, errorCode) -> {
                                // Handle error
                                ApiErrorHandler.handleUnauthorized(requireContext(), errorCode);
                            },
                            "Failed to withdraw from event"
                    ));
        });
    }
} 