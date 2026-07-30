package com.smartspend.ai.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.smartspend.ai.activities.AddExpenseActivity;
import com.smartspend.ai.adapters.ExpenseAdapter;
import com.smartspend.ai.databinding.FragmentExpensesBinding;
import com.smartspend.ai.models.Expense;
import com.smartspend.ai.viewmodels.ExpenseViewModel;

public class ExpensesFragment extends Fragment {

    private FragmentExpensesBinding binding;
    private ExpenseViewModel viewModel;
    private ExpenseAdapter adapter;
    private LiveData<java.util.List<Expense>> activeSource;
    private final Observer<java.util.List<Expense>> expenseObserver = expenses -> {
        if (adapter != null) adapter.submitList(expenses);
        if (binding != null) updateEmptyState(expenses == null || expenses.isEmpty());
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentExpensesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ExpenseViewModel.class);

        setupRecyclerView();
        setupSearch();
        setupCategoryFilter();
        observeExpenses();
    }

    private void setupRecyclerView() {
        adapter = new ExpenseAdapter(expense -> {
            Intent intent = new Intent(requireContext(), AddExpenseActivity.class);
            intent.putExtra("expense_id", expense.getId());
            startActivity(intent);
        });

        binding.rvExpenses.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvExpenses.setAdapter(adapter);

        // Swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh,
                                  @NonNull RecyclerView.ViewHolder target) { return false; }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                Expense expense = adapter.getExpenseAt(pos);
                if (expense != null) {
                    showDeleteDialog(expense, pos);
                }
            }
        }).attachToRecyclerView(binding.rvExpenses);
    }

    private void showDeleteDialog(Expense expense, int position) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Expense")
                .setMessage("Delete '" + expense.getTitle() + "'?")
                .setPositiveButton("Delete", (d, w) -> {
                    viewModel.deleteExpense(expense);
                    Snackbar.make(binding.getRoot(), "Expense deleted", Snackbar.LENGTH_SHORT)
                            .setAction("Undo", v -> viewModel.insertExpense(expense))
                            .show();
                })
                .setNegativeButton("Cancel", (d, w) -> adapter.notifyItemChanged(position))
                .setOnCancelListener(d -> adapter.notifyItemChanged(position))
                .show();
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    observeAllExpenses();
                } else {
                    observeSource(viewModel.searchExpenses(query));

                }
            }
        });
    }

    private void setupCategoryFilter() {
        String[] categories = {"All", "Food", "Travel", "Shopping", "Bills", "Entertainment", "Healthcare"};
        for (String cat : categories) {
            Chip chip = new Chip(requireContext());
            chip.setText(cat);
            chip.setCheckable(true);
            chip.setChecked("All".equals(cat));
            chip.setOnCheckedChangeListener((btn, checked) -> {
                if (checked) {
                    filterByCategory(cat);
                    uncheckOtherChips(chip);
                }
            });
            binding.chipGroupFilter.addView(chip);
        }
    }

    private void uncheckOtherChips(Chip selected) {
        for (int i = 0; i < binding.chipGroupFilter.getChildCount(); i++) {
            View child = binding.chipGroupFilter.getChildAt(i);
            if (child instanceof Chip && child != selected) {
                ((Chip) child).setChecked(false);
            }
        }
    }

    private void filterByCategory(String category) {
        if ("All".equals(category)) {
            observeAllExpenses();
        } else {
            observeSource(viewModel.getExpensesByCategory(category));

        }
    }

    private void observeExpenses() {
        observeAllExpenses();
    }

    private void observeAllExpenses() {
        observeSource(viewModel.getAllExpenses());

    }

    private void observeSource(LiveData<java.util.List<Expense>> source) {
        if (activeSource == source) return;
        if (activeSource != null) activeSource.removeObserver(expenseObserver);
        activeSource = source;
        activeSource.observe(getViewLifecycleOwner(), expenseObserver);
    }
    private void updateEmptyState(boolean isEmpty) {
        binding.layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.rvExpenses.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
