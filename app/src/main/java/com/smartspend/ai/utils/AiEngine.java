package com.smartspend.ai.utils;

import com.smartspend.ai.models.AiInsight;
import com.smartspend.ai.models.Expense;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AiEngine {

    /**
     * Generate AI insights comparing current vs last month
     */
    public static List<AiInsight> generateInsights(
            List<Expense> currentMonthExpenses,
            List<Expense> lastMonthExpenses,
            double totalBudget) {

        List<AiInsight> insights = new ArrayList<>();

        if (currentMonthExpenses == null) currentMonthExpenses = new ArrayList<>();
        if (lastMonthExpenses == null) lastMonthExpenses = new ArrayList<>();

        List<Expense> comparableLastMonth = filterThroughDay(lastMonthExpenses, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        double currentTotal = sumExpenses(currentMonthExpenses);
        double lastTotal = sumExpenses(comparableLastMonth);

        Map<String, Double> currentByCategory = groupByCategory(currentMonthExpenses);
        Map<String, Double> lastByCategory = groupByCategory(comparableLastMonth);

        // 1. Overall spending change
        if (lastTotal > 0) {
            double change = ((currentTotal - lastTotal) / lastTotal) * 100;
            if (change > 20) {
                AiInsight insight = new AiInsight(
                        AiInsight.TYPE_OVERSPENDING,
                        "Spending Spike Detected",
                        String.format(Locale.getDefault(),
                                "Your spending is up %.0f%% compared to last month. Time to review your expenses.", change),
                        "warning",
                        2
                );
                insight.setPercentageChange(change);
                insights.add(insight);
            } else if (change < -10) {
                AiInsight insight = new AiInsight(
                        AiInsight.TYPE_ACHIEVEMENT,
                        "Great Savings! 🎉",
                        String.format(Locale.getDefault(),
                                "You spent %.0f%% less than last month. Keep it up!", Math.abs(change)),
                        "trophy",
                        0
                );
                insight.setPercentageChange(change);
                insights.add(insight);
            }
        }

        // 2. Category-specific insights
        String[] categories = {"Food", "Travel", "Shopping", "Bills", "Entertainment", "Healthcare"};
        for (String cat : categories) {
            double current = currentByCategory.getOrDefault(cat, 0.0);
            double last = lastByCategory.getOrDefault(cat, 0.0);
            if (last > 0 && current > 0) {
                double change = ((current - last) / last) * 100;
                if (change > 50) {
                    AiInsight insight = new AiInsight(
                            AiInsight.TYPE_OVERSPENDING,
                            cat + " Spending Spike",
                            String.format(Locale.getDefault(),
                                    "%s spending rose by %.0f%% versus the same period last month.", cat, change),
                            getCategoryIcon(cat),
                            2
                    );
                    insight.setCategory(cat);
                    insight.setPercentageChange(change);
                    insights.add(insight);
                } else if (change > 35) {
                    AiInsight insight = new AiInsight(
                            AiInsight.TYPE_TREND,
                            cat + " Spending Increased",
                            String.format(Locale.getDefault(),
                                    "You spent %.0f%% more on %s versus the same period last month.", change, cat.toLowerCase()),
                            getCategoryIcon(cat),
                            1
                    );
                    insight.setCategory(cat);
                    insight.setPercentageChange(change);
                    insights.add(insight);
                }
            }
        }

        // 3. Budget alert
        if (totalBudget > 0) {
            double percentUsed = (currentTotal / totalBudget) * 100;
            if (percentUsed >= 80 && percentUsed < 100) {
                insights.add(new AiInsight(
                        AiInsight.TYPE_PREDICTION,
                        "Budget Almost Exhausted",
                        String.format(Locale.getDefault(),
                                "You've used %.0f%% of your monthly budget. Spend carefully for the rest of the month.", percentUsed),
                        "budget",
                        2
                ));
            } else if (percentUsed >= 100) {
                insights.add(new AiInsight(
                        AiInsight.TYPE_OVERSPENDING,
                        "Budget Exceeded!",
                        String.format(Locale.getDefault(),
                                "You've exceeded your monthly budget by ₹%.2f. Review your spending habits.", currentTotal - totalBudget),
                        "alert",
                        2
                ));
            }
        }

        // 4. Savings suggestion
        if (currentTotal > 0 && lastTotal > 0) {
            double avgSpending = (currentTotal + lastTotal) / 2;
            double potentialSavings = avgSpending * 0.15;
            insights.add(new AiInsight(
                    AiInsight.TYPE_SAVINGS,
                    "Savings Opportunity",
                    String.format(Locale.getDefault(),
                            "Based on your spending patterns, you could save up to ₹%.0f by reducing discretionary expenses by 15%%.", potentialSavings),
                    "savings",
                    0
            ));
        }

        // 5. Highest spending category
        if (!currentByCategory.isEmpty()) {
            String highestCat = getHighestCategory(currentByCategory);
            double highestAmount = currentByCategory.getOrDefault(highestCat, 0.0);
            double percentage = currentTotal > 0 ? (highestAmount / currentTotal) * 100 : 0;
            if (percentage > 40) {
                insights.add(new AiInsight(
                        AiInsight.TYPE_TREND,
                        highestCat + " is Your Biggest Expense",
                        String.format(Locale.getDefault(),
                                "%.0f%% of your spending goes to %s. Consider diversifying your budget allocation.", percentage, highestCat.toLowerCase()),
                        getCategoryIcon(highestCat),
                        1
                ));
            }
        }

        // 6. Future prediction
        Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (dayOfMonth > 5 && currentTotal > 0) {
            double dailyAvg = currentTotal / dayOfMonth;
            double projectedTotal = dailyAvg * daysInMonth;
            if (totalBudget > 0 && projectedTotal > totalBudget * 1.1) {
                insights.add(new AiInsight(
                        AiInsight.TYPE_PREDICTION,
                        "Spending Forecast",
                        String.format(Locale.getDefault(),
                                "At your current pace, you'll spend ₹%.0f this month — %.0f%% over your budget.", projectedTotal, ((projectedTotal - totalBudget) / totalBudget) * 100),
                        "forecast",
                        1
                ));
            }
        }

        return insights;
    }

    /**
     * Auto-categorize an expense based on merchant name or description
     */
    public static String autoCategorizeMerchant(String merchantName) {
        if (merchantName == null || merchantName.isEmpty()) return Expense.CATEGORY_OTHER;

        String lower = merchantName.toLowerCase();

        // Food
        if (containsAny(lower, "restaurant", "cafe", "pizza", "burger", "hotel",
                "food", "kitchen", "biryani", "swiggy", "zomato", "dhaba",
                "bakery", "chai", "tea", "coffee", "starbucks", "mcdonalds",
                "kfc", "dominos", "subway", "ice cream")) {
            return Expense.CATEGORY_FOOD;
        }

        // Travel
        if (containsAny(lower, "uber", "ola", "rapido", "irctc", "air", "flight",
                "railways", "metro", "bus", "petrol", "fuel", "parking",
                "travel", "makemytrip", "goibibo", "indigo", "spicejet",
                "cab", "taxi", "auto")) {
            return Expense.CATEGORY_TRAVEL;
        }

        // Shopping
        if (containsAny(lower, "amazon", "flipkart", "myntra", "ajio", "nykaa",
                "mall", "store", "shop", "market", "supermarket", "bigbasket",
                "grofers", "blinkit", "zepto", "reliance", "dmart")) {
            return Expense.CATEGORY_SHOPPING;
        }

        // Bills
        if (containsAny(lower, "electricity", "water", "gas", "internet", "broadband",
                "airtel", "jio", "bsnl", "vi", "recharge", "bill", "utility",
                "emi", "loan", "insurance", "rent", "maintenance")) {
            return Expense.CATEGORY_BILLS;
        }

        // Entertainment
        if (containsAny(lower, "netflix", "amazon prime", "hotstar", "spotify",
                "youtube", "cinema", "movie", "theatre", "game", "pvr", "inox",
                "entertainment", "concert", "event", "gym", "fitness")) {
            return Expense.CATEGORY_ENTERTAINMENT;
        }

        // Healthcare
        if (containsAny(lower, "pharmacy", "medical", "hospital", "clinic",
                "doctor", "medicine", "health", "apollo", "medplus",
                "diagnostic", "lab", "test", "dental", "optical")) {
            return Expense.CATEGORY_HEALTHCARE;
        }

        return Expense.CATEGORY_OTHER;
    }

    private static boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) return true;
        }
        return false;
    }

    private static List<Expense> filterThroughDay(List<Expense> expenses, int dayOfMonth) {
        List<Expense> comparable = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (Expense expense : expenses) {
            calendar.setTimeInMillis(expense.getDate());
            if (calendar.get(Calendar.DAY_OF_MONTH) <= dayOfMonth) comparable.add(expense);
        }
        return comparable;
    }
    private static double sumExpenses(List<Expense> expenses) {
        double total = 0;
        for (Expense e : expenses) total += e.getAmount();
        return total;
    }

    private static Map<String, Double> groupByCategory(List<Expense> expenses) {
        Map<String, Double> map = new HashMap<>();
        for (Expense e : expenses) {
            String cat = e.getCategory() != null ? e.getCategory() : "Other";
            map.put(cat, map.getOrDefault(cat, 0.0) + e.getAmount());
        }
        return map;
    }

    private static String getHighestCategory(Map<String, Double> categoryMap) {
        String highest = "";
        double maxAmount = 0;
        for (Map.Entry<String, Double> entry : categoryMap.entrySet()) {
            if (entry.getValue() > maxAmount) {
                maxAmount = entry.getValue();
                highest = entry.getKey();
            }
        }
        return highest;
    }

    private static String getCategoryIcon(String category) {
        switch (category) {
            case "Food": return "food";
            case "Travel": return "travel";
            case "Shopping": return "shopping";
            case "Bills": return "bills";
            case "Entertainment": return "entertainment";
            case "Healthcare": return "health";
            default: return "expense";
        }
    }
}
