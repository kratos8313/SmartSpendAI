package com.smartspend.ai.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.smartspend.ai.database.CategoryTotal;
import com.smartspend.ai.databinding.FragmentAnalyticsBinding;
import com.smartspend.ai.models.Expense;
import com.smartspend.ai.utils.CategoryUtils;
import com.smartspend.ai.utils.CurrencyUtils;
import com.smartspend.ai.viewmodels.ExpenseViewModel;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsFragment extends Fragment {

    private FragmentAnalyticsBinding binding;
    private ExpenseViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAnalyticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ExpenseViewModel.class);

        setupCharts();
        observeData();
    }

    private void setupCharts() {
        // Pie Chart styling
        PieChart pieChart = binding.pieChart;
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setDrawEntryLabels(false);
        pieChart.setCenterText("Spending\nBreakdown");
        pieChart.setCenterTextSize(14f);
        pieChart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
        pieChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        pieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry pe = (PieEntry) e;
                binding.tvSelectedCategory.setText(pe.getLabel() + ": " +
                        String.format("%.1f%%", pe.getValue()));
            }
            @Override
            public void onNothingSelected() {
                binding.tvSelectedCategory.setText("Tap a slice for details");
            }
        });

        // Bar Chart styling
        BarChart barChart = binding.barChart;
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(true);
        barChart.getLegend().setEnabled(false);
        barChart.setFitBars(true);
        barChart.animateY(800);
    }

    private void observeData() {
        // Pie chart - category breakdown
        viewModel.getCategoryTotalsThisMonth().observe(getViewLifecycleOwner(), totals -> {
            if (totals != null && !totals.isEmpty()) {
                updatePieChart(totals);
                updateSummaryCards(totals);
            }
        });

        // Monthly total
        viewModel.getCurrentMonthTotal().observe(getViewLifecycleOwner(), total -> {
            double amount = total != null ? total : 0;
            binding.tvTotalThisMonth.setText(CurrencyUtils.formatAmount(amount));
        });

        viewModel.getLastMonthTotal().observe(getViewLifecycleOwner(), total -> {
            double amount = total != null ? total : 0;
            binding.tvTotalLastMonth.setText(CurrencyUtils.formatAmount(amount));
        });

        // Weekly bar chart
        loadWeeklyData();
    }

    private void updatePieChart(List<CategoryTotal> totals) {
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (CategoryTotal ct : totals) {
            if (ct.total > 0) {
                entries.add(new PieEntry((float) ct.total, ct.category));
                colors.add(CategoryUtils.getCategoryColor(ct.category));
            }
        }

        if (entries.isEmpty()) return;

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(8f);
        dataSet.setValueFormatter(new PercentFormatter(binding.pieChart));
        dataSet.setValueTextSize(11f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        binding.pieChart.setData(data);
        binding.pieChart.invalidate();
        binding.pieChart.animateY(1000);
    }

    private void updateSummaryCards(List<CategoryTotal> totals) {
        if (totals.isEmpty()) return;

        // Highest category
        CategoryTotal highest = totals.get(0);
        binding.tvHighestCategory.setText(highest.category);
        binding.tvHighestAmount.setText(CurrencyUtils.formatCompact(highest.total));
        binding.ivHighestIcon.setImageResource(CategoryUtils.getCategoryIcon(highest.category));

        // Category list
        StringBuilder sb = new StringBuilder();
        for (CategoryTotal ct : totals) {
            sb.append(ct.category).append(": ")
                    .append(CurrencyUtils.formatCompact(ct.total)).append("\n");
        }
        binding.tvCategoryBreakdown.setText(sb.toString().trim());
    }

    private void loadWeeklyData() {
        List<BarEntry> entries = new ArrayList<>();
        String[] weekLabels = {"4 wks ago", "3 wks ago", "2 wks ago", "Last week", "This week"};

        for (int i = 4; i >= 0; i--) {
            final int weekIndex = i;
            final int barIndex = 4 - i;
            viewModel.getWeeklyTotal(i).observe(getViewLifecycleOwner(), total -> {
                entries.add(new BarEntry(barIndex, (float) (total != null ? total : 0)));
                if (entries.size() == 5) {
                    entries.sort((a, b) -> Float.compare(a.getX(), b.getX()));
                    updateBarChart(entries, weekLabels);
                }
            });
        }
    }

    private void updateBarChart(List<BarEntry> entries, String[] labels) {
        BarDataSet dataSet = new BarDataSet(entries, "Weekly Spending");
        dataSet.setColor(requireContext().getColor(com.smartspend.ai.R.color.colorPrimary));
        dataSet.setValueTextSize(10f);
        dataSet.setDrawValues(true);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);

        binding.barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        binding.barChart.getXAxis().setLabelCount(5);
        binding.barChart.setData(data);
        binding.barChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
