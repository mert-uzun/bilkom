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
import com.bilkom.model.AdminUser;
import com.bilkom.network.ApiService;
import com.bilkom.network.RetrofitClient;
import retrofit2.Call;

class AdminUserAdapter extends ListAdapter<AdminUser, AdminUserAdapter.VH> {
    AdminUserAdapter(){ super(DIFF); }
    static class VH extends RecyclerView.ViewHolder {
        TextView tvEmail, tvRole; View btnActive, btnRole;
        VH(View v){ super(v);
            tvEmail = v.findViewById(R.id.tvEmail);
            tvRole = v.findViewById(R.id.tvRole);
            btnActive = v.findViewById(R.id.btnToggleActive);
            btnRole = v.findViewById(R.id.btnToggleRole);
        }
    }
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int vType){
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_user,p,false));
    }
    @Override public void onBindViewHolder(@NonNull VH h, int pos){
        AdminUser u = getItem(pos);
        h.tvEmail.setText(u.getEmail());
        h.tvRole.setText(u.getRole());
        ApiService api = RetrofitClient.getInstance().getApiService();
        h.btnActive.setOnClickListener(v-> api.toggleUserActive(u.getId()).enqueue(VoidCb.get(v)));
        h.btnRole.setOnClickListener(v-> api.toggleUserRole(u.getId()).enqueue(VoidCb.get(v)));
    }
    private static final DiffUtil.ItemCallback<AdminUser> DIFF = new DiffUtil.ItemCallback<>(){
        public boolean areItemsTheSame(AdminUser a, AdminUser b){ return a.getId()==b.getId(); }
        public boolean areContentsTheSame(AdminUser a, AdminUser b){ return a.equals(b); }
    };
}