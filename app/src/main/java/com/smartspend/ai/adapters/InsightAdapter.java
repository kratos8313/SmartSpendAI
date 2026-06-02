package com.smartspend.ai.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.smartspend.ai.R;
import com.smartspend.ai.databinding.ItemInsightBinding;
import com.smartspend.ai.models.AiInsight;

public class InsightAdapter extends ListAdapter<AiInsight, InsightAdapter.InsightViewHolder> {

    public InsightAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<AiInsight> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<AiInsight>() {
                @Override
                public boolean areItemsTheSame(@NonNull AiInsight a, @NonNull AiInsight b) {
                    return a.getTitle().equals(b.getTitle());
                }

                @Override
                public boolean areContentsTheSame(@NonNull AiInsight a, @NonNull AiInsight b) {
                    return a.getMessage().equals(b.getMessage());
                }
            };

    @NonNull
    @Override
    public InsightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInsightBinding binding = ItemInsightBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new InsightViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull InsightViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class InsightViewHolder extends RecyclerView.ViewHolder {
        private final ItemInsightBinding binding;

        InsightViewHolder(ItemInsightBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(AiInsight insight) {
            binding.tvInsightTitle.setText(insight.getTitle());
            binding.tvInsightMessage.setText(insight.getMessage());

            // Set icon and color based on type and severity
            int iconRes;
            int bgColor;

            switch (insight.getType()) {
                case AiInsight.TYPE_OVERSPENDING:
                    iconRes = R.drawable.ic_warning;
                    bgColor = binding.getRoot().getContext().getColor(R.color.insight_warning);
                    break;
                case AiInsight.TYPE_SAVINGS:
                    iconRes = R.drawable.ic_savings;
                    bgColor = binding.getRoot().getContext().getColor(R.color.insight_success);
                    break;
                case AiInsight.TYPE_ACHIEVEMENT:
                    iconRes = R.drawable.ic_trophy;
                    bgColor = binding.getRoot().getContext().getColor(R.color.insight_success);
                    break;
                case AiInsight.TYPE_PREDICTION:
                    iconRes = R.drawable.ic_forecast;
                    bgColor = binding.getRoot().getContext().getColor(R.color.insight_info);
                    break;
                default:
                    iconRes = R.drawable.ic_trend;
                    bgColor = binding.getRoot().getContext().getColor(R.color.insight_neutral);
                    break;
            }

            binding.ivInsightIcon.setImageResource(iconRes);
            binding.cardInsight.setCardBackgroundColor(bgColor);

            // Severity indicator
            int severityColor;
            switch (insight.getSeverity()) {
                case 2: severityColor = binding.getRoot().getContext().getColor(R.color.expense_red); break;
                case 1: severityColor = binding.getRoot().getContext().getColor(R.color.expense_orange); break;
                default: severityColor = binding.getRoot().getContext().getColor(R.color.expense_green); break;
            }
            binding.viewSeverityBar.setBackgroundColor(severityColor);
        }
    }
}
