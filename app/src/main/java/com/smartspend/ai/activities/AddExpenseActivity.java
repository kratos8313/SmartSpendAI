package com.smartspend.ai.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.smartspend.ai.R;
import com.smartspend.ai.databinding.ActivityAddExpenseBinding;
import com.smartspend.ai.models.Expense;
import com.smartspend.ai.utils.CategoryUtils;
import com.smartspend.ai.utils.DateUtils;
import com.smartspend.ai.utils.CurrencyUtils;
import com.smartspend.ai.utils.MoneyUtils;
import com.smartspend.ai.utils.FxRateService;
import com.smartspend.ai.viewmodels.ExpenseViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {

    private ActivityAddExpenseBinding binding;
    private ExpenseViewModel viewModel;
    private long selectedDate = System.currentTimeMillis();
    private String selectedCategory = Expense.CATEGORY_FOOD;
    private String editExpenseId = null;
    private Expense existingExpense;
    private String selectedCurrency;
    private List<String> currencyCodes;

    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {});

    private final ActivityResultLauncher<Intent> voiceLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ArrayList<String> matches = result.getData()
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (matches != null && !matches.isEmpty()) {
                        parseVoiceInput(matches.get(0));
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        selectedCurrency = getIntent().getStringExtra("currency");
        try { selectedCurrency = CurrencyUtils.codeFromDisplayName(selectedCurrency); }
        catch (IllegalArgumentException ignored) { selectedCurrency = CurrencyUtils.getAccountCurrency(); }
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setupCategoryChips();
        setupPaymentDropdown();
        setupCurrencyDropdown();
        setupDatePicker();
        setupButtons();

        // Check if editing existing expense
        editExpenseId = getIntent().getStringExtra("expense_id");
        if (editExpenseId != null) {
            getSupportActionBar().setTitle("Edit Expense");
            loadExpenseForEdit();
        } else {
            getSupportActionBar().setTitle("Add Expense");
            updateDateDisplay();

            // Pre-fill from receipt scanner
            double scannedAmount = getIntent().getDoubleExtra("amount", 0);
            String scannedMerchant = getIntent().getStringExtra("merchant");
            String scannedCategory = getIntent().getStringExtra("category");
            long scannedDate = getIntent().getLongExtra("date", 0);

            if (scannedDate > 0) {
                selectedDate = scannedDate;
                updateDateDisplay();
            }
            if (scannedAmount > 0) {
                binding.etAmount.setText(CurrencyUtils.formatInputAmount(scannedAmount, selectedCurrency));
            }
            if (scannedMerchant != null && !scannedMerchant.isEmpty()) {
                binding.etTitle.setText(scannedMerchant);
            }
            if (scannedCategory != null) {
                setSelectedCategory(scannedCategory);
            }
        }
    }

    private void updateAmountHint() {
        binding.tilAmount.setHint("Amount (" + CurrencyUtils.getSymbol(selectedCurrency) + ")");
        binding.actvExpenseCurrency.setText(CurrencyUtils.getDisplayName(selectedCurrency), false);
    }

    private void setupCurrencyDropdown() {
        currencyCodes = java.util.Arrays.asList(CurrencyUtils.getSupportedCodes());
        String[] labels = currencyCodes.stream().map(CurrencyUtils::getDisplayName).toArray(String[]::new);
        binding.actvExpenseCurrency.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, labels));
        binding.actvExpenseCurrency.setOnItemClickListener((parent, view, position, id) -> {
            selectedCurrency = CurrencyUtils.codeFromDisplayName(parent.getItemAtPosition(position));
            binding.tilExpenseCurrency.setHelperText("Foreign amounts are converted to your account currency for totals");
            updateAmountHint();
        });
        updateAmountHint();
    }

    private void setupCategoryChips() {
        String[] categories = CategoryUtils.getAllCategories();
        for (String category : categories) {
            Chip chip = new Chip(this);
            chip.setText(category);
            chip.setCheckable(true);
            chip.setChipIconResource(CategoryUtils.getCategoryIcon(category));
            chip.setTag(category);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedCategory = category;
                    uncheckOtherChips(chip);
                }
            });
            binding.chipGroupCategories.addView(chip);
        }
        // Select first chip by default
        if (binding.chipGroupCategories.getChildCount() > 0) {
            ((Chip) binding.chipGroupCategories.getChildAt(0)).setChecked(true);
        }
    }

    private void uncheckOtherChips(Chip selected) {
        for (int i = 0; i < binding.chipGroupCategories.getChildCount(); i++) {
            View child = binding.chipGroupCategories.getChildAt(i);
            if (child instanceof Chip && child != selected) {
                ((Chip) child).setChecked(false);
            }
        }
    }

    private void setSelectedCategory(String category) {
        selectedCategory = category;
        for (int i = 0; i < binding.chipGroupCategories.getChildCount(); i++) {
            View child = binding.chipGroupCategories.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                chip.setChecked(category.equals(chip.getTag()));
            }
        }
    }

    private void setupPaymentDropdown() {
        String[] paymentModes = CategoryUtils.getAllPaymentModes();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, paymentModes);
        binding.actvPaymentMode.setAdapter(adapter);
        binding.actvPaymentMode.setText(paymentModes[0], false);
    }

    private void setupDatePicker() {
        binding.btnSelectDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(selectedDate);
            new DatePickerDialog(this, (view, year, month, day) -> {
                cal.set(year, month, day);
                selectedDate = cal.getTimeInMillis();
                updateDateDisplay();
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void updateDateDisplay() {
        binding.tvSelectedDate.setText(DateUtils.formatDate(selectedDate));
    }

    private void setupButtons() {
        binding.btnSaveExpense.setOnClickListener(v -> saveExpense());
        binding.btnScanReceipt.setOnClickListener(v -> openReceiptScanner());
        binding.btnVoiceInput.setOnClickListener(v -> startVoiceInput());
    }

    private void saveExpense() {
        try { selectedCurrency = CurrencyUtils.codeFromDisplayName(binding.actvExpenseCurrency.getText()); }
        catch (IllegalArgumentException error) {
            binding.tilExpenseCurrency.setError("Choose a currency from the list");
            return;
        }
        binding.tilExpenseCurrency.setError(null);
        String amountText = binding.etAmount.getText() == null ? "" : binding.etAmount.getText().toString().trim();
        String title = binding.etTitle.getText() == null ? "" : binding.etTitle.getText().toString().trim();
        if (TextUtils.isEmpty(amountText)) { binding.tilAmount.setError("Please enter an amount"); return; }
        if (TextUtils.isEmpty(title)) { binding.tilTitle.setError("Please enter a title"); return; }
        double enteredAmount;
        try { enteredAmount = Double.parseDouble(amountText); }
        catch (NumberFormatException error) { binding.tilAmount.setError("Invalid amount"); return; }
        if (!MoneyUtils.isPositive(enteredAmount)) { binding.tilAmount.setError("Amount must be greater than zero"); return; }

        Expense expense = editExpenseId != null && existingExpense != null ? existingExpense : new Expense();
        expense.setTitle(title);
        expense.setCategory(selectedCategory);
        expense.setDate(selectedDate);
        expense.setPaymentMode(binding.actvPaymentMode.getText().toString());
        expense.setNotes(binding.etNotes.getText() == null ? "" : binding.etNotes.getText().toString().trim());
        expense.setLocation(binding.etLocation.getText() == null ? "" : binding.etLocation.getText().toString().trim());
        expense.setTags(binding.etTags.getText() == null ? "" : binding.etTags.getText().toString().trim());
        expense.setOriginalCurrency(selectedCurrency);
        expense.setOriginalAmount(MoneyUtils.normalize(enteredAmount, selectedCurrency));

        String accountCurrency = CurrencyUtils.getAccountCurrency();
        if (selectedCurrency.equals(accountCurrency)) {
            applyRateAndPersist(expense, enteredAmount, 1.0, selectedDate, "identity");
            return;
        }
        setConversionLoading(true);
        FxRateService.getRate(this, selectedCurrency, accountCurrency, selectedDate, new FxRateService.Callback() {
            @Override public void onSuccess(FxRateService.Rate rate) {
                long rateTimestamp = selectedDate;
                try { rateTimestamp = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(rate.date).getTime(); }
                catch (Exception ignored) { }
                String source = "Frankfurter blended daily reference rates" + (rate.cached ? " (cached)" : "");
                applyRateAndPersist(expense, enteredAmount, rate.value, rateTimestamp, source);
            }
            @Override public void onFailure(String message) {
                setConversionLoading(false);
                Toast.makeText(AddExpenseActivity.this, message + " The expense was not saved.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void applyRateAndPersist(Expense expense, double enteredAmount, double rate,
            long rateTimestamp, String source) {
        expense.setCurrency(CurrencyUtils.getAccountCurrency());
        expense.setAmount(MoneyUtils.normalize(enteredAmount * rate, CurrencyUtils.getAccountCurrency()));
        expense.setExchangeRate(rate);
        expense.setExchangeRateTimestamp(rateTimestamp);
        expense.setExchangeRateSource(source);
        if (editExpenseId != null) {
            viewModel.updateExpense(expense);
            Toast.makeText(this, "Expense updated!", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.insertExpense(expense);
            Toast.makeText(this, "Expense added!", Toast.LENGTH_SHORT).show();
        }
        setConversionLoading(false);
        finish();
        overridePendingTransition(R.anim.fade_in_fast, R.anim.slide_down);
    }

    private void setConversionLoading(boolean loading) {
        binding.btnSaveExpense.setEnabled(!loading);
        binding.btnSaveExpense.setText(loading ? "Converting..." : "Save Expense");
    }
    private void openReceiptScanner() {
        Intent scanner = new Intent(this, ReceiptScannerActivity.class);
        scanner.putExtra("currency", selectedCurrency);
        startActivity(scanner);
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say: 'Spent 500 on food at McDonald's'");
        try {
            voiceLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Voice input not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void parseVoiceInput(String voiceText) {
        // Parse patterns like "spent 500 on food" or "500 rupees restaurant"
        String lower = voiceText.toLowerCase();

        // Extract amount
        java.util.regex.Pattern amountPattern = java.util.regex.Pattern.compile("(\\d+(?:\\.\\d{1,2})?)");
        java.util.regex.Matcher matcher = amountPattern.matcher(lower);
        if (matcher.find()) {
            binding.etAmount.setText(matcher.group(1));
        }

        // Extract category
        String[] categories = {"food", "travel", "shopping", "bills", "entertainment", "healthcare"};
        for (String cat : categories) {
            if (lower.contains(cat)) {
                setSelectedCategory(cat.substring(0, 1).toUpperCase() + cat.substring(1));
                break;
            }
        }

        // Set remaining text as title
        binding.etTitle.setText(voiceText);
        Toast.makeText(this, "Voice: " + voiceText, Toast.LENGTH_SHORT).show();
    }

    private void loadExpenseForEdit() {
        viewModel.getExpenseById(editExpenseId).observe(this, expense -> {
            if (expense != null) {
                existingExpense = expense;
                boolean hasOriginal = expense.getOriginalAmount() > 0 && expense.getOriginalCurrency() != null;
                selectedCurrency = hasOriginal ? expense.getOriginalCurrency() : expense.getCurrency();
                if (selectedCurrency == null) selectedCurrency = CurrencyUtils.getAccountCurrency();
                updateAmountHint();
                binding.etAmount.setText(CurrencyUtils.formatInputAmount(
                        hasOriginal ? expense.getOriginalAmount() : expense.getAmount(), selectedCurrency));
                if (hasOriginal && !selectedCurrency.equals(expense.getCurrency()) && expense.getExchangeRate() > 0) {
                    binding.tilExpenseCurrency.setHelperText(String.format(Locale.US,
                            "Stored rate: 1 %s = %.6f %s · %s", selectedCurrency, expense.getExchangeRate(),
                            expense.getCurrency(), DateUtils.formatDate(expense.getExchangeRateTimestamp())));
                }
                binding.etTitle.setText(expense.getTitle());
                binding.etNotes.setText(expense.getNotes());
                binding.etLocation.setText(expense.getLocation());
                binding.etTags.setText(expense.getTags());
                selectedDate = expense.getDate();
                updateDateDisplay();
                setSelectedCategory(expense.getCategory());
                binding.actvPaymentMode.setText(expense.getPaymentMode(), false);
            }
        });
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
