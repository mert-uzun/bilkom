package com.bilkom.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.bilkom.R;
import com.bilkom.model.Event;
import java.util.List;

public class PastEventAdapter extends EventAdapter {
    private OnReportClickListener reportClickListener;

    public interface OnReportClickListener {
        void onReportClick(Event event);
    }

    public PastEventAdapter(Context context, List<Event> eventList, OnReportClickListener reportClickListener) {
        super(context, eventList, null); // No join click listener
        this.reportClickListener = reportClickListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new PastEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof PastEventViewHolder) {
            PastEventViewHolder pastHolder = (PastEventViewHolder) holder;
            pastHolder.setupReportButton(eventList.get(position));
        }
    }

    class PastEventViewHolder extends EventViewHolder {
        public PastEventViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setupReportButton(Event event) {
            // Change button to report button
            joinButton.setText("Report");
            joinButton.setBackgroundTintList(ColorStateList.valueOf(
                itemView.getContext().getColor(R.color.red)));
            
            joinButton.setOnClickListener(v -> {
                if (reportClickListener != null) {
                    reportClickListener.onReportClick(event);
                }
            });
        }
    }
} 