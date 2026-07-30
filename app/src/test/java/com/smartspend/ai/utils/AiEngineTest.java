package com.smartspend.ai.utils;

import static org.junit.Assert.*;
import com.smartspend.ai.models.Expense;
import org.junit.Test;
import java.util.*;

public class AiEngineTest {
    private Expense expense(double amount, String category, int monthOffset, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, monthOffset);
        calendar.set(Calendar.DAY_OF_MONTH, Math.min(day, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)));
        Expense expense = new Expense();
        expense.setAmount(amount);
        expense.setCategory(category);
        expense.setDate(calendar.getTimeInMillis());
        return expense;
    }

    @Test public void categorizesKnownMerchants() {
        assertEquals(Expense.CATEGORY_FOOD, AiEngine.autoCategorizeMerchant("Zomato Order"));
        assertEquals(Expense.CATEGORY_TRAVEL, AiEngine.autoCategorizeMerchant("Uber India"));
        assertEquals(Expense.CATEGORY_OTHER, AiEngine.autoCategorizeMerchant("Unknown Vendor"));
    }

    @Test public void comparesEquivalentMonthToDatePeriods() {
        int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        List<Expense> current = Collections.singletonList(expense(100, Expense.CATEGORY_FOOD, 0, Math.max(1, today - 1)));
        List<Expense> previous = Collections.singletonList(
                expense(100, Expense.CATEGORY_FOOD, -1, Math.max(1, today - 1)));
        assertTrue(AiEngine.generateInsights(current, previous, 1000).stream()
                .noneMatch(i -> "Spending Spike Detected".equals(i.getTitle())));
    }
}