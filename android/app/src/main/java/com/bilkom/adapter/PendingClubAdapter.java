package com.bilkom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bilkom.R;
import com.bilkom.model.PendingClub;
import com.bilkom.network.ApiService;
import com.bilkom.network.RetrofitClient;
import com.bilkom.util.VoidCb;

public class PendingClubAdapter extends ListAdapter<PendingClub, PendingClubAdapter.VH> {
    private final ApiService api;

    public PendingClubAdapter() {
        super(DIFF);
        api = RetrofitClient.getInstance().getApiService();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pending_club, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        PendingClub c = getItem(pos);
        h.tvName.setText(c.getName());
        h.tvDescription.setText(c.getDescription());
        h.btnApprove.setOnClickListener(v -> api.approveClub(c.getId()).enqueue(VoidCb.get(v)));
        h.btnReject.setOnClickListener(v -> api.rejectClub(c.getId()).enqueue(VoidCb.get(v)));
    }

    private static final DiffUtil.ItemCallback<PendingClub> DIFF = new DiffUtil.ItemCallback<PendingClub>() {
        @Override
        public boolean areItemsTheSame(@NonNull PendingClub a, @NonNull PendingClub b) {
            return a.getId() == b.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull PendingClub a, @NonNull PendingClub b) {
            return a.equals(b);
        }
    };

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription;
        Button btnApprove, btnReject;

        VH(@NonNull View v) {
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvDescription = v.findViewById(R.id.tvDescription);
            btnApprove = v.findViewById(R.id.btnApprove);
            btnReject = v.findViewById(R.id.btnReject);
        }
    }
}