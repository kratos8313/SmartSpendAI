package com.smartspend.ai.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.smartspend.ai.R;
import com.smartspend.ai.databinding.ItemExpenseBinding;
import com.smartspend.ai.models.Expense;
import com.smartspend.ai.utils.CategoryUtils;
import com.smartspend.ai.utils.CurrencyUtils;
import com.smartspend.ai.utils.DateUtils;

public class ExpenseAdapter extends ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder> {

    private final OnExpenseClickListener listener;

    public interface OnExpenseClickListener {
        void onExpenseClick(Expense expense);
    }

    public ExpenseAdapter(OnExpenseClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Expense> DIFF_CALLBACK = new DiffUtil.ItemCallback<Expense>() {
        @Override
        public boolean areItemsTheSame(@NonNull Expense oldItem, @NonNull Expense newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Expense oldItem, @NonNull Expense newItem) {
            return oldItem.getAmount() == newItem.getAmount()
                    && oldItem.getTitle().equals(newItem.getTitle())
                    && oldItem.getCategory().equals(newItem.getCategory());
        }
    };

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExpenseBinding binding = ItemExpenseBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ExpenseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public Expense getExpenseAt(int position) {
        if (position >= 0 && position < getCurrentList().size()) {
            return getItem(position);
        }
        return null;
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private final ItemExpenseBinding binding;

        ExpenseViewHolder(ItemExpenseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Expense expense) {
            binding.tvExpenseTitle.setText(expense.getTitle());
            binding.tvExpenseAmount.setText(CurrencyUtils.formatAmount(expense.getAmount(), expense.getCurrency()));
            binding.tvExpenseDate.setText(DateUtils.getRelativeDate(expense.getDate()));
            binding.tvExpenseCategory.setText(expense.getCategory());
            binding.tvPaymentMode.setText(expense.getPaymentMode());

            // Category icon and color
            binding.ivCategoryIcon.setImageResource(CategoryUtils.getCategoryIcon(expense.getCategory()));
            binding.cardCategoryIcon.setCardBackgroundColor(
                    CategoryUtils.getCategoryColor(expense.getCategory()));

            // Notes chip
            if (expense.getNotes() != null && !expense.getNotes().isEmpty()) {
                binding.tvNotes.setVisibility(View.VISIBLE);
                binding.tvNotes.setText(expense.getNotes());
            } else {
                binding.tvNotes.setVisibility(View.GONE);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onExpenseClick(expense);
            });
        }
    }
}
