package com.smartspend.ai.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.smartspend.ai.adapters.InsightAdapter;
import com.smartspend.ai.databinding.FragmentInsightsBinding;
import com.smartspend.ai.models.AiInsight;
import com.smartspend.ai.models.Expense;
import com.smartspend.ai.utils.AiEngine;
import com.smartspend.ai.utils.CurrencyUtils;
import com.smartspend.ai.viewmodels.BudgetViewModel;
import com.smartspend.ai.viewmodels.ExpenseViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InsightsFragment extends Fragment {

    private FragmentInsightsBinding binding;
    private ExpenseViewModel expenseViewModel;
    private BudgetViewModel budgetViewModel;
    private InsightAdapter insightAdapter;

    private List<Expense> currentMonthExpenses = new ArrayList<>();
    private List<Expense> lastMonthExpenses = new ArrayList<>();
    private double currentBudget = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentInsightsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        expenseViewModel = new ViewModelProvider(requireActivity()).get(ExpenseViewModel.class);
        budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);

        setupRecyclerView();
        observeData();
    }

    private void setupRecyclerView() {
        insightAdapter = new InsightAdapter();
        binding.rvInsights.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvInsights.setAdapter(insightAdapter);
    }

    private void observeData() {
        expenseViewModel.getThisMonthExpenses().observe(getViewLifecycleOwner(), expenses -> {
            currentMonthExpenses = expenses != null ? expenses : new ArrayList<>();
            updateStats();
            generateInsights();
        });

        expenseViewModel.getLastMonthExpenses().observe(getViewLifecycleOwner(), expenses -> {
            lastMonthExpenses = expenses != null ? expenses : new ArrayList<>();
            generateInsights();
        });

        budgetViewModel.getCurrentMonthBudget().observe(getViewLifecycleOwner(), budget -> {
            currentBudget = budget != null ? budget.getTotalBudget() : 0;
            generateInsights();
        });

        expenseViewModel.getCurrentMonthTotal().observe(getViewLifecycleOwner(), total -> {
            double amount = total != null ? total : 0;
            binding.tvCurrentSpend.setText(CurrencyUtils.formatAmount(amount));
        });

        expenseViewModel.getLastMonthTotal().observe(getViewLifecycleOwner(), total -> {
            double amount = total != null ? total : 0;
            binding.tvLastMonthSpend.setText(CurrencyUtils.formatAmount(amount));

            expenseViewModel.getCurrentMonthTotal().observe(getViewLifecycleOwner(), currentTotal -> {
                double current = currentTotal != null ? currentTotal : 0;
                if (amount > 0) {
                    double change = ((current - amount) / amount) * 100;
                    binding.tvSpendingChange.setText(String.format(Locale.getDefault(),
                            "%+.0f%%", change));
                    binding.tvSpendingChange.setTextColor(requireContext().getColor(
                            change > 0 ? com.smartspend.ai.R.color.expense_red
                                    : com.smartspend.ai.R.color.expense_green));
                }
            });
        });
    }

    private void updateStats() {
        int count = currentMonthExpenses.size();
        binding.tvExpenseCount.setText(count + " expenses");

        if (!currentMonthExpenses.isEmpty()) {
            double total = currentMonthExpenses.stream().mapToDouble(Expense::getAmount).sum();
            double avg = total / count;
            binding.tvAvgExpense.setText(CurrencyUtils.formatCompact(avg) + " avg");

            // Most frequent category
            java.util.Map<String, Long> freq = new java.util.HashMap<>();
            for (Expense e : currentMonthExpenses) {
                String cat = e.getCategory() != null ? e.getCategory() : "Other";
                freq.put(cat, freq.getOrDefault(cat, 0L) + 1);
            }
            String topCat = freq.entrySet().stream()
                    .max(java.util.Map.Entry.comparingByValue())
                    .map(java.util.Map.Entry::getKey).orElse("N/A");
            binding.tvTopCategory.setText(topCat);
        }
    }

    private void generateInsights() {
        binding.progressLoading.setVisibility(View.VISIBLE);
        List<AiInsight> insights = AiEngine.generateInsights(
                currentMonthExpenses, lastMonthExpenses, currentBudget);
        binding.progressLoading.setVisibility(View.GONE);

        insightAdapter.submitList(insights);

        boolean isEmpty = insights.isEmpty();
        binding.layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.rvInsights.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
