package com.bilkom.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bilkom.R;
import com.bilkom.model.PendingClub;
import com.bilkom.network.ApiService;
import com.bilkom.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for managing the approval of pending clubs. Extends {@link AppCompatActivity}
 * to provide functionality for displaying a list of clubs awaiting approval.
 * Handles UI setup, API calls to fetch pending clubs, and error handling for network issues.
 * 
 * @author Sıla Bozkurt
 */
public class AdminClubApprovalActivity extends AppCompatActivity {
    private PendingClubAdapter adapter;

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_admin_club_approval);

        RecyclerView rv = findViewById(R.id.rvClubs);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PendingClubAdapter();
        rv.setAdapter(adapter);

        ApiService api = RetrofitClient.getInstance().getApiService();
        api.getPendingClubs().enqueue(new Callback<List<PendingClub>>() {
            @Override public void onResponse(Call<List<PendingClub>> call, Response<List<PendingClub>> res) {
                if(res.isSuccessful() && res.body()!=null) adapter.submitList(res.body());
                else Toast.makeText(AdminClubApprovalActivity.this,"Could not load clubs",Toast.LENGTH_SHORT).show();
            }
            @Override public void onFailure(Call<List<PendingClub>> call, Throwable t) {
                Toast.makeText(AdminClubApprovalActivity.this,"Network error",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
