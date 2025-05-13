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
import com.bilkom.utils.VoidCb;
import java.util.HashMap;
import java.util.Map;
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
        
        // For toggling user active status
        h.btnActive.setOnClickListener(v-> {
            Map<String, Boolean> payload = new HashMap<>();
            payload.put("active", !u.isActive()); // Toggle the current status
            api.setUserActive(u.getId(), payload).enqueue(VoidCb.get(v));
        });
        
        // For toggling user role (since there's no direct method in API)
        h.btnRole.setOnClickListener(v-> {
            // Get the user first
            api.getUser(u.getId()).enqueue(new retrofit2.Callback<com.bilkom.model.User>() {
                @Override
                public void onResponse(Call<com.bilkom.model.User> call, retrofit2.Response<com.bilkom.model.User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        com.bilkom.model.User user = response.body();
                        // Toggle between USER and ADMIN roles
                        String newRole = "USER";
                        if (u.getRole().equals("USER")) {
                            newRole = "ADMIN";
                        }
                        user.setRole(newRole);
                        // Update the user with new role
                        api.updateUser(u.getId(), user).enqueue(VoidCb.get(v));
                    }
                }
                
                @Override
                public void onFailure(Call<com.bilkom.model.User> call, Throwable t) {
                    // Handle failure
                    VoidCb.get(v).onFailure(null, t);
                }
            });
        });
    }
    private static final DiffUtil.ItemCallback<AdminUser> DIFF = new DiffUtil.ItemCallback<>(){
        public boolean areItemsTheSame(AdminUser a, AdminUser b){ return a.getId()==b.getId(); }
        public boolean areContentsTheSame(AdminUser a, AdminUser b){ return a.equals(b); }
    };
}