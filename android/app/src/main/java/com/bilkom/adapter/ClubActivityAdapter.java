package com.bilkom.adapter;

import android.content.Context;
import android.graphics.Color;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bilkom.R;
import com.bilkom.model.Event;
import com.bilkom.network.ApiService;
import com.bilkom.network.RetrofitClient;
import com.bilkom.utils.SecureStorage;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClubActivityAdapter extends EventAdapter {
    private OnClubActivityClickListener listener;

    public interface OnClubActivityClickListener {
        void onClubActivityClick(Event event);
    }

    public ClubActivityAdapter(Context context, List<Event> clubActivities, OnClubActivityClickListener listener) {
        super(context, clubActivities, event -> {
            if (listener != null) {
                listener.onClubActivityClick(event);
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_club_activity, parent, false);
        return new ClubActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof ClubActivityViewHolder) {
            ClubActivityViewHolder clubHolder = (ClubActivityViewHolder) holder;
            clubHolder.setupJoinButton(eventList.get(position));
        }
    }

    class ClubActivityViewHolder extends EventViewHolder {
        private SecureStorage secureStorage;
        private ApiService apiService;
        private LinearLayout tagsContainer;

        public ClubActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            secureStorage = new SecureStorage(itemView.getContext());
            apiService = RetrofitClient.getInstance().getApiService();
            tagsContainer = itemView.findViewById(R.id.tagsContainer);
        }

        public void setupJoinButton(Event event) {
            super.bind(event);
            
            // Clear previous tags
            tagsContainer.removeAllViews();
            if (event.getTags() != null) {
                for (String tag : event.getTags()) {
                    TextView tagView = new TextView(itemView.getContext());
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

            // Update join button state
            updateJoinButtonState(event);

            joinButton.setOnClickListener(v -> {
                if (event.isJoined()) {
                    leaveEvent(event);
                } else {
                    joinEvent(event);
                }
            });
        }

        private void updateJoinButtonState(Event event) {
            if (event.isJoined()) {
                joinButton.setText("Leave");
                joinButton.setBackgroundTintList(ColorStateList.valueOf(
                    itemView.getContext().getColor(R.color.red)));
            } else {
                joinButton.setText("Join");
                joinButton.setBackgroundTintList(ColorStateList.valueOf(
                    itemView.getContext().getColor(R.color.green)));
            }
        }

        private void joinEvent(Event event) {
            String token = secureStorage.getAuthToken();
            apiService.joinEvent(event.getEventId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        event.setJoined(true);
                        event.setCurrentParticipantsNumber(event.getCurrentParticipantsNumber() + 1);
                        updateJoinButtonState(event);
                        quotaTextView.setText("Activity Quota: " + event.getCurrentParticipantsNumber() + "/" + event.getMaxParticipants());
                    } else {
                        Toast.makeText(itemView.getContext(), 
                            "Failed to join event", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(itemView.getContext(), 
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void leaveEvent(Event event) {
            String token = secureStorage.getAuthToken();
            apiService.withdrawEvent(event.getEventId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        event.setJoined(false);
                        event.setCurrentParticipantsNumber(event.getCurrentParticipantsNumber() - 1);
                        updateJoinButtonState(event);
                        quotaTextView.setText("Activity Quota: " + event.getCurrentParticipantsNumber() + "/" + event.getMaxParticipants());
                    } else {
                        Toast.makeText(itemView.getContext(), 
                            "Failed to leave event", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(itemView.getContext(), 
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
} 