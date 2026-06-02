package com.smartspend.ai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.smartspend.ai.R;
import com.smartspend.ai.databinding.ActivitySettingsBinding;
import com.smartspend.ai.utils.ThemeUtils;

import java.util.concurrent.Executor;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }

        loadSettings();
        setupListeners();
    }

    private void loadSettings() {
        binding.switchDarkMode.setChecked(ThemeUtils.isDarkModeEnabled(this));

        // Load biometric availability
        BiometricManager bm = BiometricManager.from(this);
        boolean biometricAvailable = bm.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                == BiometricManager.BIOMETRIC_SUCCESS;
        binding.switchBiometric.setEnabled(biometricAvailable);
        if (!biometricAvailable) {
            binding.tvBiometricStatus.setText("Biometric not available on this device");
        }

        // User info
        var user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            binding.tvUserEmail.setText(user.getEmail());
            binding.tvUserName.setText(user.getDisplayName() != null ? user.getDisplayName() : "User");
        }
    }

    private void setupListeners() {
        binding.switchDarkMode.setOnCheckedChangeListener((btn, checked) -> {
            ThemeUtils.toggleDarkMode(this, checked);
            recreate();
        });

        binding.switchBiometric.setOnCheckedChangeListener((btn, checked) -> {
            if (checked) authenticateBiometric();
        });

        binding.btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        binding.btnChangePassword.setOnClickListener(v -> {
            var user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && user.getEmail() != null) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(user.getEmail())
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(this, "Password reset email sent!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        binding.btnExportPdf.setOnClickListener(v ->
                Toast.makeText(this, "Export feature - generating PDF...", Toast.LENGTH_SHORT).show());
    }

    private void authenticateBiometric() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt prompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        Toast.makeText(SettingsActivity.this, "Biometric enabled!", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onAuthenticationFailed() {
                        binding.switchBiometric.setChecked(false);
                    }
                });

        BiometricPrompt.PromptInfo info = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Enable Biometric Login")
                .setSubtitle("Authenticate to enable fingerprint/face unlock")
                .setNegativeButtonText("Cancel")
                .build();
        prompt.authenticate(info);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
