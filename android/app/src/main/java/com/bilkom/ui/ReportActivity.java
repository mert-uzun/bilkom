package com.bilkom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.bilkom.ui.BaseActivity;
import com.bilkom.R;
import com.bilkom.network.RetrofitClient;
import com.bilkom.utils.SecureStorage;
import com.bilkom.model.ReportRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportActivity extends BaseActivity {

    private EditText reasonEditText;
    private Button submitReportButton;
    private SecureStorage secureStorage;
    private Long eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setupNavigationDrawer();
        getLayoutInflater().inflate(R.layout.activity_report, findViewById(R.id.contentFrame));

        // Get event ID from intent
        eventId = getIntent().getLongExtra("eventId", -1);
        if (eventId == -1) {
            Toast.makeText(this, "Invalid event", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        reasonEditText = findViewById(R.id.reasonEditText);
        submitReportButton = findViewById(R.id.submitReportButton);
        secureStorage = new SecureStorage(this);

        submitReportButton.setOnClickListener(v -> {
            submitReport();
        });
    }

    private void submitReport() {
        String reason = reasonEditText.getText().toString().trim();
        if (reason.isEmpty()) {
            reasonEditText.setError("Please enter a reason");
            return;
        }

        Toast loadingToast = Toast.makeText(this, "Submitting report...", Toast.LENGTH_SHORT);
        loadingToast.show();

        ReportRequest request = new ReportRequest(reason);
        RetrofitClient.getInstance().getApiService()
                .reportEvent(eventId, request)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        loadingToast.cancel();
                        if (response.isSuccessful()) {
                            Toast.makeText(ReportActivity.this, 
                                "Report submitted successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ReportActivity.this, 
                                "Failed to submit report", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        loadingToast.cancel();
                        Toast.makeText(ReportActivity.this, 
                            "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
