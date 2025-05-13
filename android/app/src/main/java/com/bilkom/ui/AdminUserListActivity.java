package com.bilkom.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bilkom.R;
import com.bilkom.model.AdminUser;
import com.bilkom.network.ApiService;
import com.bilkom.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for displaying a list of admin users. Extends {@link AppCompatActivity} 
 * to provide functionality for managing and displaying a list of users in a 
 * RecyclerView. Handles API calls to fetch user data and updates the UI accordingly.
 * 
 * This activity uses {@link AdminUserAdapter} to bind user data to the RecyclerView 
 * and {@link RetrofitClient} to perform network operations for fetching the user list.
 * 
 * @author Sıla Bozkurt
 */
public class AdminUserListActivity extends AppCompatActivity {
    private AdminUserAdapter adapter;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_admin_user_list);

        RecyclerView rv = findViewById(R.id.rvUsers);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminUserAdapter();
        rv.setAdapter(adapter);

        ApiService api = RetrofitClient.getInstance().getApiService();
        api.getAllUsers().enqueue(new Callback<List<AdminUser>>() {
            @Override public void onResponse(Call<List<AdminUser>> call, Response<List<AdminUser>> res) {
                if (res.isSuccessful() && res.body()!=null) adapter.submitList(res.body());
                else Toast.makeText(AdminUserListActivity.this,"Failed to load users",Toast.LENGTH_SHORT).show();
            }
            @Override public void onFailure(Call<List<AdminUser>> call, Throwable t) {
                Toast.makeText(AdminUserListActivity.this,"Network error",Toast.LENGTH_SHORT).show();
            }
        });
    }
}