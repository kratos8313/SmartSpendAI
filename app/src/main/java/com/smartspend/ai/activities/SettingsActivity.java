package com.smartspend.ai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.smartspend.ai.databinding.ActivitySettingsBinding;
import com.smartspend.ai.utils.CurrencyUtils;
import com.smartspend.ai.utils.SecurityUtils;
import com.smartspend.ai.utils.ThemeUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    private List<String> currencyCodes;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) { getSupportActionBar().setDisplayHomeAsUpEnabled(true); getSupportActionBar().setTitle("Settings"); }
        loadSettings();
        setupListeners();
    }

    private void loadSettings() {
        binding.switchDarkMode.setChecked(ThemeUtils.isDarkModeEnabled(this));
        BiometricManager bm = BiometricManager.from(this);
        boolean available = bm.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS;
        binding.switchBiometric.setEnabled(available);
        binding.switchBiometric.setChecked(available && SecurityUtils.isBiometricEnabled(this));
        if (!available) binding.tvBiometricStatus.setText("Biometric not available on this device");
        var user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            binding.tvUserEmail.setText(user.getEmail());
            binding.tvUserName.setText(user.getDisplayName() != null ? user.getDisplayName() : "User");
        }
        currencyCodes = java.util.Arrays.asList(CurrencyUtils.getSupportedCodes());
        String[] labels = currencyCodes.stream().map(CurrencyUtils::getDisplayName).toArray(String[]::new);
        binding.actvCurrency.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, labels));
        int selected = Math.max(0, currencyCodes.indexOf(CurrencyUtils.getAccountCurrency()));
        binding.actvCurrency.setText(labels[selected], false);
    }

    private void setupListeners() {
        binding.actvCurrency.setOnItemClickListener((parent, view, position, id) -> {
            String code = currencyCodes.get(position);
            CurrencyUtils.setAccountCurrency(this, code);
            var user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                Map<String, Object> update = new HashMap<>(); update.put("currency", code);
                FirebaseFirestore.getInstance().collection("users").document(user.getUid()).set(update, SetOptions.merge());
            }
            Toast.makeText(this, "Currency updated. Existing values are not exchange-rate converted.", Toast.LENGTH_LONG).show();
        });
        binding.switchDarkMode.setOnCheckedChangeListener((button, checked) -> { ThemeUtils.toggleDarkMode(this, checked); recreate(); });
        binding.switchBiometric.setOnCheckedChangeListener((button, checked) -> {
            if (checked == SecurityUtils.isBiometricEnabled(this)) return;
            if (checked) authenticateBiometric(); else SecurityUtils.setBiometricEnabled(this, false);
        });
        binding.btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, AuthActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        binding.btnChangePassword.setOnClickListener(v -> {
            var user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && user.getEmail() != null) FirebaseAuth.getInstance().sendPasswordResetEmail(user.getEmail())
                    .addOnSuccessListener(unused -> Toast.makeText(this, "Password reset email sent!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(error -> Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show());
        });
        binding.btnExportPdf.setOnClickListener(v -> startActivity(new Intent(this, MonthlyReportActivity.class)));
    }

    private void authenticateBiometric() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt prompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                SecurityUtils.setBiometricEnabled(SettingsActivity.this, true); binding.switchBiometric.setChecked(true);
            }
            @Override public void onAuthenticationFailed() { SecurityUtils.setBiometricEnabled(SettingsActivity.this, false); binding.switchBiometric.setChecked(false); }
            @Override public void onAuthenticationError(int code, @NonNull CharSequence message) { SecurityUtils.setBiometricEnabled(SettingsActivity.this, false); binding.switchBiometric.setChecked(false); }
        });
        prompt.authenticate(new BiometricPrompt.PromptInfo.Builder().setTitle("Enable Biometric Login")
                .setSubtitle("Authenticate to enable fingerprint/face unlock").setNegativeButtonText("Cancel").build());
    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) { onBackPressed(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
