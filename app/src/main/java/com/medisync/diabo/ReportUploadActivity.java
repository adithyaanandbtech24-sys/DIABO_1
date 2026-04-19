package com.medisync.diabo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.medisync.diabo.databinding.ActivityReportUploadBinding;
import com.medisync.diabo.service.ReportService;
import com.medisync.diabo.model.MedicalReport;

public class ReportUploadActivity extends AppCompatActivity {

    private ActivityReportUploadBinding binding;
    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            this::handleSelectedImage
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportUploadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.btnPickImage.setOnClickListener(v -> galleryLauncher.launch("image/*"));
        
        binding.btnTakePhoto.setOnClickListener(v -> {
            // Camera launcher omitted for simplicity
            Toast.makeText(this, "Camera not implemented for this demo", Toast.LENGTH_SHORT).show();
        });
    }

    private void handleSelectedImage(Uri uri) {
        if (uri == null) return;

        binding.ivPreview.setImageURI(uri);
        binding.ivPreview.setVisibility(View.VISIBLE);
        binding.tvInstruction.setVisibility(View.GONE);
        binding.btnPickImage.setVisibility(View.GONE);
        binding.btnTakePhoto.setVisibility(View.GONE);

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvStatus.setVisibility(View.VISIBLE);

        ReportService.processReport(this, uri, new ReportService.ReportCallback() {
            @Override
            public void onSuccess(MedicalReport report) {
                runOnUiThread(() -> {
                    Toast.makeText(ReportUploadActivity.this, "Analysis Successful!", Toast.LENGTH_LONG).show();
                    finish();
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.tvStatus.setText("Error: " + error);
                    binding.btnPickImage.setVisibility(View.VISIBLE);
                    Toast.makeText(ReportUploadActivity.this, error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
