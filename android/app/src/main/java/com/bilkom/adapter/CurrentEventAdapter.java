package com.bilkom.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import com.bilkom.R;
import com.bilkom.model.Event;
import java.util.List;

public class CurrentEventAdapter extends EventAdapter {
    private OnWithdrawClickListener withdrawClickListener;

    public interface OnWithdrawClickListener {
        void onWithdrawClick(Event event);
    }

    public CurrentEventAdapter(Context context, List<Event> eventList, OnWithdrawClickListener withdrawClickListener) {
        super(context, eventList, null); // No join click listener
        this.withdrawClickListener = withdrawClickListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new CurrentEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof CurrentEventViewHolder) {
            CurrentEventViewHolder currentHolder = (CurrentEventViewHolder) holder;
            currentHolder.setupWithdrawButton(eventList.get(position));
        }
    }

    class CurrentEventViewHolder extends EventViewHolder {
        public CurrentEventViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setupWithdrawButton(Event event) {
            // Change button to withdraw button
            joinButton.setText("Withdraw");
            joinButton.setBackgroundTintList(ColorStateList.valueOf(
                itemView.getContext().getColor(R.color.red)));
            
            joinButton.setOnClickListener(v -> {
                if (withdrawClickListener != null) {
                    withdrawClickListener.onWithdrawClick(event);
                }
            });
        }
    }
} 