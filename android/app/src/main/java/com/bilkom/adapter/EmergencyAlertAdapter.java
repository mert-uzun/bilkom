package com.bilkom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bilkom.R;
import com.bilkom.model.EmergencyAlert;
import java.util.List;

public class EmergencyAlertAdapter extends RecyclerView.Adapter<EmergencyAlertAdapter.AlertViewHolder> {
    private Context context;
    private List<EmergencyAlert> alertList;
    private OnAlertClickListener alertClickListener;

    public interface OnAlertClickListener {
        void onAlertClick(EmergencyAlert alert);
    }

    public EmergencyAlertAdapter(Context context, List<EmergencyAlert> alertList, OnAlertClickListener alertClickListener) {
        this.context = context;
        this.alertList = alertList;
        this.alertClickListener = alertClickListener;
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_emergency_alert, parent, false);
        return new AlertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        EmergencyAlert alert = alertList.get(position);
        holder.alertTitleText.setText(alert.getTitle());
        holder.alertDateText.setText(alert.getFormattedCreated());
        holder.alertDescriptionText.setText(alert.getDescription());
        holder.alertLocationText.setText("Location: " + alert.getLocation());
        
        String statusText = alert.isActive() ? "Active" : "Resolved";
        int statusColor = alert.isActive() ? 
                context.getResources().getColor(android.R.color.holo_red_light) :
                context.getResources().getColor(android.R.color.holo_green_dark);
        
        holder.alertStatusText.setText(statusText);
        holder.alertStatusText.setTextColor(statusColor);

        holder.itemView.setOnClickListener(v -> {
            if (alertClickListener != null) {
                alertClickListener.onAlertClick(alert);
            }
        });
    }

    @Override
    public int getItemCount() {
        return alertList != null ? alertList.size() : 0;
    }

    public void setAlertList(List<EmergencyAlert> alertList) {
        this.alertList = alertList;
        notifyDataSetChanged();
    }

    public static class AlertViewHolder extends RecyclerView.ViewHolder {
        TextView alertTitleText, alertDateText, alertDescriptionText, alertLocationText, alertStatusText;

        public AlertViewHolder(@NonNull View itemView) {
            super(itemView);
            alertTitleText = itemView.findViewById(R.id.alertTitleText);
            alertDateText = itemView.findViewById(R.id.alertDateText);
            alertDescriptionText = itemView.findViewById(R.id.alertDescriptionText);
            alertLocationText = itemView.findViewById(R.id.alertLocationText);
            alertStatusText = itemView.findViewById(R.id.alertStatusText);
        }
    }
} 