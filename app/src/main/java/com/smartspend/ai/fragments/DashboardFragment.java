package com.smartspend.ai.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smartspend.ai.R;
import com.smartspend.ai.activities.AddExpenseActivity;
import com.smartspend.ai.activities.ReceiptScannerActivity;
import com.smartspend.ai.activities.SettingsActivity;
import com.smartspend.ai.adapters.ExpenseAdapter;
import com.smartspend.ai.adapters.InsightAdapter;
import com.smartspend.ai.databinding.FragmentDashboardBinding;
import com.smartspend.ai.models.AiInsight;
import com.smartspend.ai.models.Budget;
import com.smartspend.ai.models.Expense;
import com.smartspend.ai.utils.AiEngine;
import com.smartspend.ai.utils.CurrencyUtils;
import com.smartspend.ai.utils.DateUtils;
import com.smartspend.ai.viewmodels.BudgetViewModel;
import com.smartspend.ai.viewmodels.ExpenseViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private ExpenseViewModel expenseViewModel;
    private BudgetViewModel budgetViewModel;
    private ExpenseAdapter recentAdapter;
    private InsightAdapter insightAdapter;

    private List<Expense> currentMonthExpenses = new ArrayList<>();
    private List<Expense> lastMonthExpenses = new ArrayList<>();
    private double currentBudget = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        expenseViewModel = new ViewModelProvider(requireActivity()).get(ExpenseViewModel.class);
        budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);

        setupGreeting();
        setupRecyclerViews();
        setupQuickActions();
        observeData();
    }

    private void setupGreeting() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user != null && user.getDisplayName() != null ?
                user.getDisplayName().split(" ")[0] : "there";

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting = hour < 12 ? "Good Morning" : hour < 17 ? "Good Afternoon" : "Good Evening";
        binding.tvGreeting.setText(greeting + ", " + name + " 👋");
        binding.tvMonthYear.setText(DateUtils.MONTH_YEAR_FORMAT.format(new java.util.Date()));
    }

    private void setupRecyclerViews() {
        recentAdapter = new ExpenseAdapter(expense -> {
            Intent intent = new Intent(requireContext(), AddExpenseActivity.class);
            intent.putExtra("expense_id", expense.getId());
            startActivity(intent);
        });
        binding.rvRecentExpenses.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRecentExpenses.setAdapter(recentAdapter);
        binding.rvRecentExpenses.setNestedScrollingEnabled(false);

        insightAdapter = new InsightAdapter();
        binding.rvInsights.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvInsights.setAdapter(insightAdapter);
    }

    private void setupQuickActions() {
        binding.btnScanReceipt.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), ReceiptScannerActivity.class)));

        binding.btnSettings.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), SettingsActivity.class)));

        binding.tvSeeAll.setOnClickListener(v -> {
            // Navigate to expenses tab
            if (getActivity() != null) {
                ((com.smartspend.ai.activities.MainActivity) getActivity()).navigateToExpenses();

            }
        });
    }

    private void observeData() {
        // Current month total
        expenseViewModel.getCurrentMonthTotal().observe(getViewLifecycleOwner(), total -> {
            double amount = total != null ? total : 0;
            binding.tvMonthlySpending.setText(CurrencyUtils.formatAmount(amount));

            if (currentBudget > 0) {
                double progress = (amount / currentBudget) * 100;
                binding.progressBudget.setProgress((int) Math.min(progress, 100));
                binding.tvBudgetStatus.setText(String.format(
                        "%.0f%% of ₹%.0f budget used", progress, currentBudget));
            }
        });

        // Recent expenses
        expenseViewModel.getRecentExpenses(5).observe(getViewLifecycleOwner(), expenses -> {
            recentAdapter.submitList(expenses);
            binding.tvNoExpenses.setVisibility(
                    expenses == null || expenses.isEmpty() ? View.VISIBLE : View.GONE);
        });

        // Current month for AI
        expenseViewModel.getThisMonthExpenses().observe(getViewLifecycleOwner(), expenses -> {
            currentMonthExpenses = expenses != null ? expenses : new ArrayList<>();
            generateInsights();
        });

        // Last month for AI comparison
        expenseViewModel.getLastMonthExpenses().observe(getViewLifecycleOwner(), expenses -> {
            lastMonthExpenses = expenses != null ? expenses : new ArrayList<>();
            generateInsights();
        });

        // Budget
        budgetViewModel.getCurrentMonthBudget().observe(getViewLifecycleOwner(), budget -> {
            if (budget != null) {
                currentBudget = budget.getTotalBudget();
                binding.tvBudgetAmount.setText("Budget: " + CurrencyUtils.formatCompact(currentBudget));
                binding.cardBudget.setVisibility(View.VISIBLE);
            } else {
                binding.cardBudget.setVisibility(View.GONE);
                currentBudget = 0;
            }
        });
    }

    private void generateInsights() {
        List<AiInsight> insights = AiEngine.generateInsights(
                currentMonthExpenses, lastMonthExpenses, currentBudget);
        insightAdapter.submitList(insights);
        binding.rvInsights.setVisibility(insights.isEmpty() ? View.GONE : View.VISIBLE);
        binding.tvInsightsEmpty.setVisibility(insights.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
