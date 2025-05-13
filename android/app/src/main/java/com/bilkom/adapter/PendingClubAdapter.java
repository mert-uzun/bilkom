package com.bilkom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bilkom.R;
import com.bilkom.model.Club;
import java.util.List;

public class PendingClubAdapter extends RecyclerView.Adapter<PendingClubAdapter.ViewHolder> {
    private List<Club> pendingClubs;
    private OnClubActionListener listener;

    public interface OnClubActionListener {
        void onApprove(Club club);
        void onReject(Club club);
    }

    public PendingClubAdapter(List<Club> pendingClubs, OnClubActionListener listener) {
        this.pendingClubs = pendingClubs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pending_club, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Club club = pendingClubs.get(position);
        holder.tvName.setText(club.getClubName());
        holder.tvDescription.setText(club.getClubDescription());
        holder.tvRequestedBy.setText("Requested by: " + club.getPresident().getUsername());

        holder.btnApprove.setOnClickListener(v -> listener.onApprove(club));
        holder.btnReject.setOnClickListener(v -> listener.onReject(club));
    }

    @Override
    public int getItemCount() {
        return pendingClubs.size();
    }

    public void updateData(List<Club> newClubs) {
        this.pendingClubs = newClubs;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvRequestedBy;
        Button btnApprove, btnReject;

        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvClubName);
            tvDescription = v.findViewById(R.id.tvDescription);
            tvRequestedBy = v.findViewById(R.id.tvRequestedBy);
            btnApprove = v.findViewById(R.id.btnApprove);
            btnReject = v.findViewById(R.id.btnReject);
        }
    }
}