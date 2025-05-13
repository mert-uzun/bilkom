package com.bilkom.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bilkom.R;
import com.bilkom.model.Event;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    protected List<Event> eventList;
    protected Context context;
    private OnJoinClickListener joinClickListener;

    public interface OnJoinClickListener {
        void onJoinClick(Event event);
    }

    public EventAdapter(Context context, List<Event> eventList, OnJoinClickListener joinClickListener) {
        this.context = context;
        this.eventList = eventList;
        this.joinClickListener = joinClickListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.bind(event);

        holder.joinButton.setOnClickListener(v -> {
            if (joinClickListener != null) {
                joinClickListener.onJoinClick(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImageView;
        TextView messageTextView, detailsTextView, quotaTextView, dateLocationTextView;
        LinearLayout tagsContainer;
        Button joinButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            detailsTextView = itemView.findViewById(R.id.detailsTextView);
            quotaTextView = itemView.findViewById(R.id.quotaTextView);
            dateLocationTextView = itemView.findViewById(R.id.dateLocationTextView);
            tagsContainer = itemView.findViewById(R.id.tagsContainer);
            joinButton = itemView.findViewById(R.id.joinButton);
        }

        public void bind(Event event) {
            messageTextView.setText(event.getEventName());
            detailsTextView.setText(event.getEventDescription());
            quotaTextView.setText("Activity Quota: " + event.getCurrentParticipantsNumber() + "/" + event.getMaxParticipants());
            
            // Display formatted date and location
            String dateTimeLocation = event.getFormattedEventDate() + " • " + event.getEventLocation();
            dateLocationTextView.setText(dateTimeLocation);
            dateLocationTextView.setVisibility(View.VISIBLE);

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
        }
    }
} 