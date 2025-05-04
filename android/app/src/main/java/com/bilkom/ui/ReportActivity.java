package com.bilkom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.bilkom.BaseActivity;
import com.bilkom.R;
import com.bilkom.network.RetrofitClient;
import com.bilkom.utils.SecureStorage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportActivity extends BaseActivity {

    private EditText eventIdEditText, reasonEditText;
    private Button submitReportButton;
    private SecureStorage secureStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setupNavigationDrawer();
        getLayoutInflater().inflate(R.layout.activity_report, findViewById(R.id.contentFrame));

        eventIdEditText = findViewById(R.id.eventIdEditText);
        reasonEditText = findViewById(R.id.reasonEditText);
        submitReportButton = findViewById(R.id.submitReportButton);
        secureStorage = new SecureStorage(this);

        submitReportButton.setOnClickListener(v -> {
            String reason = reasonEditText.getText().toString();
            Long eventId;
            try {
                eventId = Long.parseLong(eventIdEditText.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid Event ID", Toast.LENGTH_SHORT).show();
                return;
            }

            RetrofitClient.getInstance().getApiService()
                .reportPastEvent(eventId, reason)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ReportActivity.this, "Report submitted", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ReportActivity.this, "Failed to submit", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(ReportActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        });
    }
}
