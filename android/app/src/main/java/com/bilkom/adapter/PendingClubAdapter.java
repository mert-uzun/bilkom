package com.bilkom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bilkom.R;
import com.bilkom.model.PendingClub;
import com.bilkom.network.ApiService;
import com.bilkom.network.RetrofitClient;

class PendingClubAdapter extends ListAdapter<PendingClub, PendingClubAdapter.VH> {
    PendingClubAdapter(){ super(DIFF); }
    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc; View btnApprove, btnReject;
        VH(View v){ super(v);
            tvName = v.findViewById(R.id.tvClubName);
            tvDesc = v.findViewById(R.id.tvClubDesc);
            btnApprove = v.findViewById(R.id.btnApprove);
            btnReject = v.findViewById(R.id.btnReject);
        }
    }
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t){
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_pending_club,p,false));
    }
    @Override public void onBindViewHolder(@NonNull VH h, int pos){
        PendingClub c = getItem(pos);
        h.tvName.setText(c.getName());
        h.tvDesc.setText(c.getDescription());
        ApiService api = RetrofitClient.getInstance().getApiService();
        h.btnApprove.setOnClickListener(v-> api.approveClub(c.getId()).enqueue(VoidCb.get(v)));
        h.btnReject.setOnClickListener(v-> api.rejectClub(c.getId()).enqueue(VoidCb.get(v)));
    }
    private static final DiffUtil.ItemCallback<PendingClub> DIFF = new DiffUtil.ItemCallback<>(){
        public boolean areItemsTheSame(PendingClub a, PendingClub b){ return a.getId()==b.getId(); }
        public boolean areContentsTheSame(PendingClub a, PendingClub b){ return a.equals(b); }
    };
}