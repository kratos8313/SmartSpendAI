package com.smartspend.ai.utils;

import com.smartspend.ai.models.Expense;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public final class MonthlyReportSummary {
    public final double total;
    public final double previousTotal;
    public final double budget;
    public final int expenseCount;
    public final double changePercent;
    public final double budgetPercent;
    public final String topCategory;
    public final double topCategoryTotal;
    public final String currencyCode;
    public final Map<String, Double> categoryTotals;

    private MonthlyReportSummary(double total, double previousTotal, double budget, int expenseCount,
            double changePercent, double budgetPercent, String topCategory, double topCategoryTotal,
            String currencyCode, Map<String, Double> categoryTotals) {
        this.total = total;
        this.previousTotal = previousTotal;
        this.budget = budget;
        this.expenseCount = expenseCount;
        this.changePercent = changePercent;
        this.budgetPercent = budgetPercent;
        this.topCategory = topCategory;
        this.topCategoryTotal = topCategoryTotal;
        this.currencyCode = currencyCode;
        this.categoryTotals = categoryTotals;
    }

    public static MonthlyReportSummary from(List<Expense> current, List<Expense> previous,
            double budget, String currencyCode) {
        List<Expense> safeCurrent = current == null ? java.util.Collections.emptyList() : current;
        List<Expense> safePrevious = previous == null ? java.util.Collections.emptyList() : previous;
        double total = safeCurrent.stream().mapToDouble(Expense::getAmount).sum();
        double previousTotal = safePrevious.stream().mapToDouble(Expense::getAmount).sum();
        Map<String, Double> categories = safeCurrent.stream().collect(Collectors.groupingBy(
                item -> item.getCategory() == null || item.getCategory().isBlank() ? "Other" : item.getCategory(),
                LinkedHashMap::new, Collectors.summingDouble(Expense::getAmount)));
        String top = "No spending yet";
        double topTotal = 0;
        for (Map.Entry<String, Double> entry : categories.entrySet()) {
            if (entry.getValue() > topTotal) {
                top = entry.getKey();
                topTotal = entry.getValue();
            }
        }
        double change = previousTotal > 0 ? ((total - previousTotal) / previousTotal) * 100 : 0;
        double budgetPercent = budget > 0 ? (total / budget) * 100 : 0;
        return new MonthlyReportSummary(total, previousTotal, budget, safeCurrent.size(), change,
                budgetPercent, top, topTotal, currencyCode, categories);
    }

    public String deterministicNarrative() {
        if (expenseCount == 0) return "No expenses were recorded this month. Add expenses to unlock a useful monthly analysis.";
        String comparison = previousTotal > 0
                ? String.format(Locale.US, "Spending is %.0f%% %s than last month.", Math.abs(changePercent),
                    changePercent > 0 ? "higher" : "lower")
                : "There is no prior-month baseline yet.";
        String budgetStatus = budget > 0
                ? String.format(Locale.US, "%.0f%% of the monthly budget has been used.", budgetPercent)
                : "Set a monthly budget to track progress.";
        return comparison + " " + topCategory + " is the largest category at "
                + CurrencyUtils.formatAmount(topCategoryTotal, currencyCode) + ". " + budgetStatus;
    }

    public String toGroundingPrompt() {
        StringBuilder categories = new StringBuilder();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            if (categories.length() > 0) categories.append(", ");
            categories.append(entry.getKey()).append("=")
                    .append(String.format(Locale.US, "%.2f", entry.getValue()));
        }
        return "You are SmartSpend's monthly spending coach. Use ONLY the facts below. "
                + "Do not infer income, identity, intent, or missing data. Do not give investment, tax, or credit advice. "
                + "Write exactly three short bullet points: trend, budget status, and one practical spending action. "
                + "If a comparison or budget is unavailable, say so. Currency=" + currencyCode
                + "; currentTotal=" + String.format(Locale.US, "%.2f", total)
                + "; previousTotal=" + String.format(Locale.US, "%.2f", previousTotal)
                + "; budget=" + String.format(Locale.US, "%.2f", budget)
                + "; expenseCount=" + expenseCount + "; categories={" + categories + "}.";
    }
}
