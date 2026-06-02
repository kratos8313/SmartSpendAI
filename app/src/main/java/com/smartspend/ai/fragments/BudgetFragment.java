package com.smartspend.ai.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.smartspend.ai.databinding.FragmentBudgetBinding;
import com.smartspend.ai.models.Budget;
import com.smartspend.ai.models.Expense;
import com.smartspend.ai.utils.CurrencyUtils;
import com.smartspend.ai.utils.NotificationUtils;
import com.smartspend.ai.viewmodels.BudgetViewModel;
import com.smartspend.ai.viewmodels.ExpenseViewModel;

import java.util.Calendar;
import java.util.Locale;

public class BudgetFragment extends Fragment {

    private FragmentBudgetBinding binding;
    private BudgetViewModel budgetViewModel;
    private ExpenseViewModel expenseViewModel;
    private Budget currentBudget;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBudgetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);
        expenseViewModel = new ViewModelProvider(requireActivity()).get(ExpenseViewModel.class);

        setupSaveButton();
        observeData();
    }

    private void setupSaveButton() {
        binding.btnSaveBudget.setOnClickListener(v -> saveBudget());
    }

    private void saveBudget() {
        double total = parseAmount(binding.etTotalBudget.getText());
        if (total <= 0) {
            binding.tilTotalBudget.setError("Please enter a valid budget");
            return;
        }

        Budget budget = currentBudget != null ? currentBudget : new Budget();
        Calendar cal = Calendar.getInstance();
        budget.setMonth(cal.get(Calendar.MONTH) + 1);
        budget.setYear(cal.get(Calendar.YEAR));
        budget.setTotalBudget(total);
        budget.setFoodBudget(parseAmount(binding.etFoodBudget.getText()));
        budget.setTravelBudget(parseAmount(binding.etTravelBudget.getText()));
        budget.setShoppingBudget(parseAmount(binding.etShoppingBudget.getText()));
        budget.setBillsBudget(parseAmount(binding.etBillsBudget.getText()));
        budget.setEntertainmentBudget(parseAmount(binding.etEntertainmentBudget.getText()));
        budget.setHealthcareBudget(parseAmount(binding.etHealthcareBudget.getText()));
        budget.setAlertAt80(binding.switchAlert80.isChecked());
        budget.setAlertAt100(binding.switchAlert100.isChecked());

        if (currentBudget != null) {
            budgetViewModel.updateBudget(budget);
        } else {
            budgetViewModel.saveBudget(budget);
        }

        Toast.makeText(requireContext(), "Budget saved successfully!", Toast.LENGTH_SHORT).show();
    }

    private void observeData() {
        budgetViewModel.getCurrentMonthBudget().observe(getViewLifecycleOwner(), budget -> {
            currentBudget = budget;
            if (budget != null) {
                populateBudgetFields(budget);
            }
        });

        expenseViewModel.getCurrentMonthTotal().observe(getViewLifecycleOwner(), total -> {
            double spent = total != null ? total : 0;
            binding.tvSpentSoFar.setText("Spent: " + CurrencyUtils.formatAmount(spent));

            if (currentBudget != null && currentBudget.getTotalBudget() > 0) {
                double budgetAmount = currentBudget.getTotalBudget();
                double remaining = budgetAmount - spent;
                double progress = (spent / budgetAmount) * 100;

                binding.progressOverallBudget.setProgress((int) Math.min(progress, 100));
                binding.tvRemainingBudget.setText(remaining >= 0 ?
                        "Remaining: " + CurrencyUtils.formatAmount(remaining) :
                        "Over by: " + CurrencyUtils.formatAmount(Math.abs(remaining)));

                // Budget alerts
                if (currentBudget.isAlertAt80() && progress >= 80 && progress < 100) {
                    NotificationUtils.showBudgetAlert(requireContext(),
                            String.format(Locale.getDefault(),
                                    "You've used %.0f%% of your monthly budget!", progress));
                } else if (currentBudget.isAlertAt100() && progress >= 100) {
                    NotificationUtils.showBudgetAlert(requireContext(),
                            "You've exceeded your monthly budget!");
                }
            }
        });

        // Category spending vs budget
        observeCategoryProgress(Expense.CATEGORY_FOOD);
        observeCategoryProgress(Expense.CATEGORY_TRAVEL);
        observeCategoryProgress(Expense.CATEGORY_SHOPPING);
        observeCategoryProgress(Expense.CATEGORY_BILLS);
        observeCategoryProgress(Expense.CATEGORY_ENTERTAINMENT);
        observeCategoryProgress(Expense.CATEGORY_HEALTHCARE);
    }

    private void observeCategoryProgress(String category) {
        expenseViewModel.getCategoryTotalThisMonth(category).observe(getViewLifecycleOwner(), total -> {
            double spent = total != null ? total : 0;
            if (currentBudget == null) return;

            double catBudget = currentBudget.getBudgetForCategory(category);
            if (catBudget <= 0) return;

            int progress = (int) Math.min((spent / catBudget) * 100, 100);
            String text = String.format(Locale.getDefault(),
                    "%s: %s / %s", category,
                    CurrencyUtils.formatCompact(spent),
                    CurrencyUtils.formatCompact(catBudget));

            updateCategoryProgressUI(category, progress, text);
        });
    }

    private void updateCategoryProgressUI(String category, int progress, String text) {
        switch (category) {
            case Expense.CATEGORY_FOOD:
                binding.progressFood.setProgress(progress);
                binding.tvFoodProgress.setText(text);
                break;
            case Expense.CATEGORY_TRAVEL:
                binding.progressTravel.setProgress(progress);
                binding.tvTravelProgress.setText(text);
                break;
            case Expense.CATEGORY_SHOPPING:
                binding.progressShopping.setProgress(progress);
                binding.tvShoppingProgress.setText(text);
                break;
            case Expense.CATEGORY_BILLS:
                binding.progressBills.setProgress(progress);
                binding.tvBillsProgress.setText(text);
                break;
            case Expense.CATEGORY_ENTERTAINMENT:
                binding.progressEntertainment.setProgress(progress);
                binding.tvEntertainmentProgress.setText(text);
                break;
            case Expense.CATEGORY_HEALTHCARE:
                binding.progressHealthcare.setProgress(progress);
                binding.tvHealthcareProgress.setText(text);
                break;
        }
    }

    private void populateBudgetFields(Budget budget) {
        setEditTextValue(binding.etTotalBudget, budget.getTotalBudget());
        setEditTextValue(binding.etFoodBudget, budget.getFoodBudget());
        setEditTextValue(binding.etTravelBudget, budget.getTravelBudget());
        setEditTextValue(binding.etShoppingBudget, budget.getShoppingBudget());
        setEditTextValue(binding.etBillsBudget, budget.getBillsBudget());
        setEditTextValue(binding.etEntertainmentBudget, budget.getEntertainmentBudget());
        setEditTextValue(binding.etHealthcareBudget, budget.getHealthcareBudget());
        binding.switchAlert80.setChecked(budget.isAlertAt80());
        binding.switchAlert100.setChecked(budget.isAlertAt100());
    }

    private void setEditTextValue(com.google.android.material.textfield.TextInputEditText et, double value) {
        if (value > 0) et.setText(String.format(Locale.getDefault(), "%.0f", value));
    }

    private double parseAmount(android.text.Editable editable) {
        if (editable == null || editable.toString().trim().isEmpty()) return 0;
        try { return Double.parseDouble(editable.toString().trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
