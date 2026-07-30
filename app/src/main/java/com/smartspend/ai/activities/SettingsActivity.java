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
import androidx.lifecycle.ViewModelProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.smartspend.ai.databinding.ActivitySettingsBinding;
import com.smartspend.ai.models.Expense;
import com.smartspend.ai.models.Budget;
import com.smartspend.ai.utils.CurrencyUtils;
import com.smartspend.ai.utils.SecurityUtils;
import com.smartspend.ai.utils.ThemeUtils;
import com.smartspend.ai.utils.FxRateService;
import com.smartspend.ai.utils.MoneyUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import com.smartspend.ai.viewmodels.ExpenseViewModel;
import com.smartspend.ai.viewmodels.BudgetViewModel;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    private List<String> currencyCodes;
    private List<Expense> allExpenses = new ArrayList<>();
    private ExpenseViewModel expenseViewModel;
    private BudgetViewModel budgetViewModel;
    private Budget currentBudget;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        expenseViewModel.getAllExpenses().observe(this, expenses -> allExpenses = expenses == null ? new ArrayList<>() : expenses);
        budgetViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
        budgetViewModel.getCurrentMonthBudget().observe(this, budget -> currentBudget = budget);
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
        binding.actvCurrency.setOnItemClickListener((parent, view, position, id) ->
                changeAccountCurrency(CurrencyUtils.codeFromDisplayName(parent.getItemAtPosition(position))));
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

    private void changeAccountCurrency(String targetCurrency) {
        String previousCurrency = CurrencyUtils.getAccountCurrency();
        if (targetCurrency.equals(previousCurrency)) return;
        binding.actvCurrency.setEnabled(false);
        binding.tilCurrency.setHelperText("Updating existing expenses to " + targetCurrency + "...");
        collectConversions(targetCurrency, previousCurrency, 0, new ArrayList<>());
    }

    private void collectConversions(String targetCurrency, String previousCurrency, int index,
            List<PendingConversion> pending) {
        if (index >= allExpenses.size()) {
            if (currentBudget == null) applyAccountCurrencyChange(targetCurrency, pending, null);
            else FxRateService.getRate(this, previousCurrency, targetCurrency, System.currentTimeMillis(), new FxRateService.Callback() {
                @Override public void onSuccess(FxRateService.Rate rate) { applyAccountCurrencyChange(targetCurrency, pending, rate); }
                @Override public void onFailure(String message) { cancelAccountCurrencyChange(previousCurrency, message); }
            });
            return;
        }
        Expense expense = allExpenses.get(index);
        double originalAmount = expense.getOriginalAmount() > 0 ? expense.getOriginalAmount() : expense.getAmount();
        String originalCurrency = expense.getOriginalCurrency() != null && !expense.getOriginalCurrency().isBlank()
                ? expense.getOriginalCurrency() : expense.getCurrency();
        if (originalCurrency == null) originalCurrency = previousCurrency;
        final String sourceCurrency = originalCurrency;
        FxRateService.getRate(this, sourceCurrency, targetCurrency, expense.getDate(), new FxRateService.Callback() {
            @Override public void onSuccess(FxRateService.Rate rate) {
                pending.add(new PendingConversion(expense, originalAmount, sourceCurrency, rate));
                collectConversions(targetCurrency, previousCurrency, index + 1, pending);
            }
            @Override public void onFailure(String message) {
                cancelAccountCurrencyChange(previousCurrency, message);
            }
        });
    }

    private void applyAccountCurrencyChange(String targetCurrency, List<PendingConversion> pending,
            FxRateService.Rate budgetRate) {
        CurrencyUtils.setAccountCurrency(this, targetCurrency);
        for (PendingConversion conversion : pending) {
            Expense expense = conversion.expense;
            expense.setCurrency(targetCurrency);
            expense.setAmount(MoneyUtils.normalize(conversion.originalAmount * conversion.rate.value, targetCurrency));
            expense.setOriginalCurrency(conversion.originalCurrency);
            expense.setOriginalAmount(MoneyUtils.normalize(conversion.originalAmount, conversion.originalCurrency));
            expense.setExchangeRate(conversion.rate.value);
            expense.setExchangeRateTimestamp(parseRateDate(conversion.rate.date, expense.getDate()));
            expense.setExchangeRateSource("Frankfurter blended daily reference rates" + (conversion.rate.cached ? " (cached)" : ""));
            expenseViewModel.updateExpense(expense);
        }
        if (currentBudget != null && budgetRate != null) {
            double rate = budgetRate.value;
            currentBudget.setTotalBudget(currentBudget.getTotalBudget() * rate);
            currentBudget.setFoodBudget(currentBudget.getFoodBudget() * rate);
            currentBudget.setTravelBudget(currentBudget.getTravelBudget() * rate);
            currentBudget.setShoppingBudget(currentBudget.getShoppingBudget() * rate);
            currentBudget.setBillsBudget(currentBudget.getBillsBudget() * rate);
            currentBudget.setEntertainmentBudget(currentBudget.getEntertainmentBudget() * rate);
            currentBudget.setHealthcareBudget(currentBudget.getHealthcareBudget() * rate);
            budgetViewModel.updateBudget(currentBudget);
        }
        updateCloudCurrency(targetCurrency);
        binding.actvCurrency.setText(CurrencyUtils.getDisplayName(targetCurrency), false);
        binding.actvCurrency.setEnabled(true);
        binding.tilCurrency.setHelperText("Existing expenses and the current budget were revalued using dated reference rates");
        Toast.makeText(this, "Account currency, totals, and budget updated.", Toast.LENGTH_LONG).show();
    }

    private void cancelAccountCurrencyChange(String previousCurrency, String message) {
        binding.actvCurrency.setText(CurrencyUtils.getDisplayName(previousCurrency), false);
        binding.actvCurrency.setEnabled(true);
        binding.tilCurrency.setHelperText("Account currency was not changed because one or more rates were unavailable");
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    private static long parseRateDate(String value, long fallback) {
        try { java.util.Date date = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).parse(value);
            return date == null ? fallback : date.getTime(); }
        catch (java.text.ParseException ignored) { return fallback; }
    }

    private void updateCloudCurrency(String code) {
        var user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        Map<String, Object> update = new HashMap<>(); update.put("currency", code);
        FirebaseFirestore.getInstance().collection("users").document(user.getUid()).set(update, SetOptions.merge());
    }

    private static final class PendingConversion {
        final Expense expense; final double originalAmount; final String originalCurrency; final FxRateService.Rate rate;
        PendingConversion(Expense expense, double originalAmount, String originalCurrency, FxRateService.Rate rate) {
            this.expense = expense; this.originalAmount = originalAmount; this.originalCurrency = originalCurrency; this.rate = rate;
        }
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
