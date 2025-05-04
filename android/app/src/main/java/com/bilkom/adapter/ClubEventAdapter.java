package com.bilkom.adapter;

import android.content.Context;
import android.graphics.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import com.bilkom.R;
import com.bilkom.model.Event;
import java.util.List;

public class ClubEventAdapter extends EventAdapter {
    private OnCancelClickListener cancelClickListener;

    public interface OnCancelClickListener {
        void onCancelClick(Event event);
    }

    public ClubEventAdapter(Context context, List<Event> eventList, OnCancelClickListener cancelClickListener) {
        super(context, eventList, null); // No join click listener
        this.cancelClickListener = cancelClickListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new ClubEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof ClubEventViewHolder) {
            ClubEventViewHolder clubHolder = (ClubEventViewHolder) holder;
            clubHolder.setupCancelButton(eventList.get(position));
        }
    }

    class ClubEventViewHolder extends EventViewHolder {
        public ClubEventViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setupCancelButton(Event event) {
            // Change button to cancel button
            joinButton.setText("Cancel Activity");
            joinButton.setBackgroundTintList(ColorStateList.valueOf(
                itemView.getContext().getColor(R.color.red)));
            
            joinButton.setOnClickListener(v -> {
                if (cancelClickListener != null) {
                    cancelClickListener.onCancelClick(event);
                }
            });
        }
    }
} 