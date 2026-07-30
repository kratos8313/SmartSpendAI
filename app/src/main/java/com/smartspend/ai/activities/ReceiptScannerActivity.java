package com.smartspend.ai.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.smartspend.ai.R;
import com.smartspend.ai.databinding.ActivityReceiptScannerBinding;
import com.smartspend.ai.utils.OcrUtils;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReceiptScannerActivity extends AppCompatActivity {

    private ActivityReceiptScannerBinding binding;
    private Uri photoUri;

    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean cameraGranted = result.getOrDefault(Manifest.permission.CAMERA, false);
                if (Boolean.TRUE.equals(cameraGranted)) {
                    openCamera();
                }
            });

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success) {
                    processImageFromUri(photoUri);
                }
            });

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    processImageFromUri(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReceiptScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Scan Receipt");
        }

        setupButtons();
    }

    private void setupButtons() {
        binding.btnCamera.setOnClickListener(v -> checkCameraPermissionAndOpen());
        binding.btnGallery.setOnClickListener(v -> galleryLauncher.launch("image/*"));
        binding.btnUseReceipt.setOnClickListener(v -> useScannedData());
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            permissionLauncher.launch(new String[]{Manifest.permission.CAMERA});
        }
    }

    private void openCamera() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File photoFile = new File(getExternalFilesDir(null), "RECEIPT_" + timestamp + ".jpg");
        photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
        cameraLauncher.launch(photoUri);
    }

    private void processImageFromUri(Uri uri) {
        try {
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            try (InputStream stream = getContentResolver().openInputStream(uri)) {
                if (stream == null) return;
                BitmapFactory.decodeStream(stream, null, bounds);
            }
            int sampleSize = 1;
            while (bounds.outWidth / sampleSize > 2048 || bounds.outHeight / sampleSize > 2048) sampleSize *= 2;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = sampleSize;
            Bitmap bitmap;
            try (InputStream stream = getContentResolver().openInputStream(uri)) {
                if (stream == null) return;
                bitmap = BitmapFactory.decodeStream(stream, null, options);
            }
            if (bitmap == null) throw new IllegalArgumentException("Unsupported image");
            showReceiptPreview(bitmap);
            scanReceiptWithOcr(bitmap);
        } catch (Exception e) {
            Toast.makeText(this, "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showReceiptPreview(Bitmap bitmap) {
        binding.ivReceipt.setImageBitmap(bitmap);
        binding.ivReceipt.setVisibility(View.VISIBLE);
        binding.tvScanHint.setVisibility(View.GONE);
    }

    private void scanReceiptWithOcr(Bitmap bitmap) {
        binding.progressScanning.setVisibility(View.VISIBLE);
        binding.tvScanStatus.setText("Scanning receipt...");
        binding.tvScanStatus.setVisibility(View.VISIBLE);
        binding.cardScanResult.setVisibility(View.GONE);

        OcrUtils.scanReceipt(bitmap, new OcrUtils.OcrCallback() {
            @Override
            public void onSuccess(OcrUtils.ScannedReceipt receipt) {
                runOnUiThread(() -> displayScanResult(receipt));
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    binding.progressScanning.setVisibility(View.GONE);
                    binding.tvScanStatus.setText("Scan failed. Try again.");
                    Toast.makeText(ReceiptScannerActivity.this,
                            "OCR Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private OcrUtils.ScannedReceipt lastReceipt;

    private void displayScanResult(OcrUtils.ScannedReceipt receipt) {
        lastReceipt = receipt;
        binding.progressScanning.setVisibility(View.GONE);
        binding.tvScanStatus.setVisibility(View.GONE);
        binding.cardScanResult.setVisibility(View.VISIBLE);

        binding.tvMerchantName.setText(receipt.merchantName != null ? receipt.merchantName : "Unknown");
        binding.tvScannedAmount.setText(receipt.amount > 0 ?
                String.format(Locale.getDefault(), "₹%.2f", receipt.amount) : "Not detected");
        binding.tvScannedDate.setText(receipt.date != null && !receipt.date.isEmpty() ?
                receipt.date : "Not detected");
        binding.tvScannedCategory.setText(receipt.suggestedCategory != null ?
                receipt.suggestedCategory : "Other");

        binding.btnUseReceipt.setVisibility(View.VISIBLE);
    }

    private void useScannedData() {
        if (lastReceipt == null) return;

        Intent intent = new Intent(this, AddExpenseActivity.class);
        intent.putExtra("amount", lastReceipt.amount);
        intent.putExtra("merchant", lastReceipt.merchantName);
        intent.putExtra("category", lastReceipt.suggestedCategory);
        if (lastReceipt.dateMillis > 0) intent.putExtra("date", lastReceipt.dateMillis);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
